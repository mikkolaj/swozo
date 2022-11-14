package com.swozo.orchestrator.cloud.software.quizapp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swozo.i18n.TranslationsProvider;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.IsolationMode;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.LinkFormatter;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.Playbook;
import com.swozo.orchestrator.cloud.software.runner.PlaybookFailed;
import com.swozo.orchestrator.cloud.storage.BucketHandler;
import com.swozo.utils.LoggingUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static com.swozo.utils.LoggingUtils.logIfSuccess;

@Service
@RequiredArgsConstructor
public class QuizAppProvisioner implements TimedSoftwareProvisioner {
    private static final String PROVISIONER_PATH_PREFIX = "/home/swozo/quiz-app";
    private static final ServiceTypeEntity SUPPORTED_SCHEDULE = ServiceTypeEntity.QUIZAPP;
    private static final int FILE_NAME_RETRIES = 10;
    private static final int PREPARATION_SECONDS = 400;
    private static final int RUNNING_SECONDS = 200;
    private static final int MINUTES_FACTOR = 60;
    private static final String QUIZ_APP_PORT = "8998";
    private static final String USER_KEY_QUERY_PARAM = "ukey";
    private static final String WORKDIR = PROVISIONER_PATH_PREFIX + "/answers";
    private static final String QUESTIONS_PATH = PROVISIONER_PATH_PREFIX + "/questions/questions.yaml";
    private final TranslationsProvider translationsProvider;
    private final ObjectMapper mapper;
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final BucketHandler bucketHandler;
    private final BackendRequestSender requestSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(
                SUPPORTED_SCHEDULE.toString(),
                QuizAppParameters.getParameterDescriptions(translationsProvider),
                Set.of(IsolationMode.SHARED)
        );
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> provision(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails resource
    ) {
        return CompletableFuture.supplyAsync(() -> {
                    logger.info("Started provisioning QuizApp on: {}", resource);
                    return handleProvisioning(resource, description, requestEntity);
                })
                .whenComplete(logIfSuccess(logger, provisioningComplete(resource)))
                .whenComplete(this::wrapExceptions)
                .thenApply(userIdToUUidMapping -> createLinks(resource, userIdToUUidMapping));
    }

    private static String provisioningComplete(VmResourceDetails resource) {
        return String.format("Successfully provisioned QuizApp on resource: %s", resource);
    }

    private void wrapExceptions(Object unused, Throwable throwable) {
        if (throwable instanceof InvalidParametersException || throwable instanceof PlaybookFailed) {
            throw new ProvisioningFailed(throwable);
        }
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> createLinks(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails vmResourceDetails
    ) {
        // unsupported, can't do this without that mapping
        return CompletableFuture.completedFuture(List.of());
    }

    public List<ActivityLinkInfo> createLinks(
            VmResourceDetails vmResourceDetails,
            Map<Long, String> userIdToUUid
    ) {
        var formattedLink = linkFormatter.getHttpLink(vmResourceDetails.publicIpAddress(), QUIZ_APP_PORT);
        return userIdToUUid.entrySet().stream()
                .map(user -> createLink(formattedLink, user.getKey(), user.getValue()))
                .toList();
    }

    private ActivityLinkInfo createLink(String baseUrl, Long userId, String uuid) {
        return new ActivityLinkInfo(
                userId,
                linkFormatter.appendQueryParams(baseUrl, Map.of(USER_KEY_QUERY_PARAM, uuid)),
                translationsProvider.t("services.quizApp.connectionInstruction")
        );
    }

    @Override
    public void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException {
        QuizAppParameters.from(dynamicParameters);
    }

    @Override
    public ServiceTypeEntity getScheduleType() {
        return SUPPORTED_SCHEDULE;
    }

    @Override
    public int getProvisioningSeconds() {
        return PREPARATION_SECONDS + RUNNING_SECONDS;
    }

    @Override
    public Optional<String> getWorkdirToSave() {
        return Optional.of(WORKDIR);
    }

    private Map<Long, String> handleProvisioning(
            VmResourceDetails resource,
            ServiceDescriptionEntity description,
            ScheduleRequestEntity scheduleRequest
    ) {
        var quizAppParams = QuizAppParameters.from(description.getDynamicProperties());
        var userData = requestSender.getUserData(description.getActivityModuleId(), scheduleRequest.getId()).join();
        var userIdToUuidMapping = createUserIdToRandomUuidMapping(userData);
        var paramsFile = createQuizAppParamsFile(quizAppParams, userIdToUuidMapping, userData);

        try {
            prepareSystem(resource, quizAppParams, paramsFile);
            runQuizApp(resource);
            return userIdToUuidMapping;
        } finally {
            cleanupAfterSystemPreparation(paramsFile);
        }
    }

    private void runQuizApp(VmResourceDetails resource) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                Playbook.RUN_QUIZ_APP,
                RUNNING_SECONDS / MINUTES_FACTOR
        );
    }

    private void prepareSystem(VmResourceDetails resource, QuizAppParameters quizAppParams, File paramsFile) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                Playbook.PREPARE_QUIZ_APP,
                List.of(
                        ansibleRunner.createUserVar("params_path", paramsFile.getAbsolutePath())
                ),
                PREPARATION_SECONDS / MINUTES_FACTOR
        );

        downloadQuestions(quizAppParams, resource).join();
    }

    private void cleanupAfterSystemPreparation(File paramsFile) {
        if (!paramsFile.delete()) {
            logger.warn("Failed to delete temp quizApp params file at " + paramsFile.getAbsolutePath());
        }
    }

    private CompletableFuture<Void> downloadQuestions(QuizAppParameters quizAppParameters, VmResourceDetails resource) {
        logger.info("Downloading quiz questions to {}", resource);
        return bucketHandler.downloadToHost(resource, quizAppParameters.questionsLocation(), QUESTIONS_PATH)
                .whenComplete(LoggingUtils.log(
                        logger,
                        String.format("Done downloading file for %s", resource),
                        String.format("Failed to download file for %s", resource)
                ));
    }

    private Map<Long, String> createUserIdToRandomUuidMapping(List<OrchestratorUserDto> userData) {
        return userData.stream()
                    .collect(Collectors.toMap(
                            OrchestratorUserDto::id,
                            x -> UUID.randomUUID().toString()
                    )
                );
    }

    private File createQuizAppParamsFile(
            QuizAppParameters quizAppParameters,
            Map<Long, String> userIdToUuid,
            List<OrchestratorUserDto> userData
    ) {
        var userMapping = userData.stream()
                .collect(Collectors.toMap(
                        userDto -> userIdToUuid.get(userDto.id()),
                        this::formatHumanReadableUserIdentifier
                ));

        for (int i = 0; i < FILE_NAME_RETRIES; i += Math.max(Math.random() * 10, 1)) {
            try {
                var file = File.createTempFile(SUPPORTED_SCHEDULE + "__params__" + i, null);
                writeJsonParamsToFile(file, new Params(quizAppParameters.quizDurationSeconds(), userMapping));
                return file;
            } catch (IOException ignored) {
                logger.warn("Failed to create temp file for quizApp");
            }
        }

        throw new ProvisioningFailed("Failed to create temp file after " + FILE_NAME_RETRIES + " retries");
    }

    private void writeJsonParamsToFile(File paramsFile, Params params) {
        try (var writer = new FileWriter(paramsFile)) {
            writer.write(new JsonMapperFacade(mapper).toJson(params));
        } catch (IOException e) {
            throw new ProvisioningFailed("Failed to write QuizApp params file to " + paramsFile.getAbsolutePath());
        }
    }

    private String formatHumanReadableUserIdentifier(OrchestratorUserDto userDto) {
        return String.format("%s %s (%s)", userDto.name(), userDto.surname(), userDto.email());
    }

    private record Params(int timeS, Map<String, String> userMapping) {}
}

