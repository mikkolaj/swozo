package com.swozo.orchestrator.cloud.software.docker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swozo.i18n.TranslationsProvider;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.IsolationMode;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.swozo.utils.LoggingUtils.logIfSuccess;

@Service
@RequiredArgsConstructor
public class DockerProvisioner implements TimedSoftwareProvisioner {
    private static final ServiceTypeEntity SUPPORTED_SCHEDULE = ServiceTypeEntity.DOCKER;
    private static final String PROVISIONER_PATH_PREFIX = "/home/swozo/docker-service";
    private static final String RESULTS_DIR = PROVISIONER_PATH_PREFIX + "/workdir";
    private static final String INPUT_FILE_DIR = PROVISIONER_PATH_PREFIX + "/input";
    private static final String DOCKER_PORT = "8999";
    private static final String CONTAINER_NAME = "swozo-custom-docker-container";
    private static final double TIMEOUT_MULTIPLIER = 1.5;
    private static final int PREPARATION_SECONDS = 400;
    private static final int MINUTES_FACTOR = 60;
    private final ObjectMapper mapper;
    private final TranslationsProvider translationsProvider;
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final BucketHandler bucketHandler;
    private final BackendRequestSender requestSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(
                "Docker",
                SUPPORTED_SCHEDULE.toString(),
                DockerParameters.getParameterDescriptions(translationsProvider),
                Set.of(IsolationMode.ISOLATED, IsolationMode.SHARED),
                DockerParameters.getConfigurationInstruction(translationsProvider),
                DockerParameters.getUsageInstruction(translationsProvider)
        );
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> provision(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails resource
    ) {
        return CompletableFuture.runAsync(() -> {
                    logger.info("Started provisioning Docker on: {}", resource);
                    handleProvisioning(resource, description);
                })
                .whenComplete(logIfSuccess(logger, provisioningComplete(resource)))
                .whenComplete(this::wrapExceptions)
                .thenCompose(x -> createLinks(requestEntity, description, resource));
    }

    private static String provisioningComplete(VmResourceDetails resource) {
        return String.format("Successfully provisioned Docker on resource: %s", resource);
    }

    private void wrapExceptions(Object unused, Throwable throwable) {
        if (throwable instanceof InvalidParametersException || throwable instanceof PlaybookFailed) {
            throw new ProvisioningFailed(throwable);
        }
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> createLinks(
            ScheduleRequestEntity scheduleRequest,
            ServiceDescriptionEntity description,
            VmResourceDetails vmResourceDetails
    ) {
        var dockerParameters = DockerParameters.from(description.getDynamicProperties());
        var formattedLink = linkFormatter.getHttpLink(vmResourceDetails.publicIpAddress(), DOCKER_PORT) +
                dockerParameters.startEndpoint().map(startEndpoint -> "/" + startEndpoint).orElse("");
        var connectionInstructions = translationsProvider.t("services.docker.instructions.connection");

        return requestSender.getUserData(description.getActivityModuleId(), scheduleRequest.getId())
                .thenApply(userData -> userData.stream()
                    .map(userDto -> new ActivityLinkInfo(userDto.id(), formattedLink, connectionInstructions))
                    .toList()
                );
    }

    @Override
    public void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException {
        DockerParameters.from(dynamicParameters);
    }

    @Override
    public ServiceTypeEntity getServiceType() {
        return SUPPORTED_SCHEDULE;
    }

    @Override
    public int getProvisioningSeconds(Map<String, String> dynamicParameters) {
        var params = DockerParameters.from(dynamicParameters);
        return PREPARATION_SECONDS + params.expectedServiceStartupSeconds() + getEstimatedImageDownloadSeconds(params);
    }

    @Override
    public Optional<String> getWorkdirToSave(Map<String, String> dynamicParameters) {
        var params = DockerParameters.from(dynamicParameters);

        if (params.resultsPathInContainer().isPresent() && params.inputFile().isPresent()) {
            var resultsDir = params.resultsPathInContainer().get();
            var inputDir = extractDirectoryFromPath(params.inputFile().get().containerFileLocationPath());

            return Optional.of(Mount.isSameDirectory(resultsDir, inputDir) ? INPUT_FILE_DIR : RESULTS_DIR);
        }

        return params.resultsPathInContainer().map(resultsPathMountedToResultsDir -> RESULTS_DIR);
    }

    private void handleProvisioning(VmResourceDetails resource, ServiceDescriptionEntity description) {
        var params = DockerParameters.from(description.getDynamicProperties());
        prepareSystem(resource);
        var mounts = downloadRequiredFilesAndPrepareMounts(params, resource);

        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                Playbook.RUN_CUSTOM_DOCKER,
                List.of(
                        ansibleRunner.createUserVar("containerName", CONTAINER_NAME),
                        ansibleRunner.createUserVar("imageName", params.publicImageName()),
                        ansibleRunner.createUserVar("servicePort", DOCKER_PORT),
                        ansibleRunner.createUserVar("portToExpose", Integer.toString(params.portToExpose())),
                        ansibleRunner.createUserVar("mounts", new JsonMapperFacade(mapper).toJson(mounts))
                ),
                getPlaybookTimeoutMinutes(description)
        );
    }

    private void prepareSystem(VmResourceDetails resource) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                Playbook.PREPARE_CUSTOM_DOCKER,
                PREPARATION_SECONDS
        );
    }

    private List<Mount> downloadRequiredFilesAndPrepareMounts(DockerParameters params, VmResourceDetails resource) {
        var mounts = new LinkedList<Mount>();
        params.inputFile().ifPresent(inputFile -> {
            var fileName = extractFileNameFromPath(inputFile.containerFileLocationPath());
            var fileDir = extractDirectoryFromPath(inputFile.containerFileLocationPath());
            bucketHandler.downloadToHost(resource, inputFile.fileLocation(), INPUT_FILE_DIR + "/" + fileName);
            mounts.add(Mount.bind(INPUT_FILE_DIR, fileDir));
        });

        params.resultsPathInContainer().ifPresent(resultsPath -> {
            // Docker can't mount same directory twice
            if (mounts.stream().noneMatch(mount -> Mount.isSameDirectory(mount.target, resultsPath))) {
                mounts.add(Mount.bind(RESULTS_DIR, resultsPath));
            }
        });

        return mounts;
    }

    private int getPlaybookTimeoutMinutes(ServiceDescriptionEntity description) {
        return (int)((getProvisioningSeconds(description.getDynamicProperties()) * TIMEOUT_MULTIPLIER) / MINUTES_FACTOR);
    }

    private int getEstimatedImageDownloadSeconds(DockerParameters dockerParameters) {
        // TODO use machine bandwidth and/or some other approximations
        final var ESTIMATED_MBPS = 10;
        return (int)(dockerParameters.imageSizeMb() / ESTIMATED_MBPS);
    }

    private String extractDirectoryFromPath(String path) {
        var pathElements = path.split("/");
        return String.join("/", Arrays.copyOfRange(pathElements, 0, pathElements.length - 1));
    }

    private String extractFileNameFromPath(String path) {
        var pathElements = path.split("/");
        return pathElements[pathElements.length - 1];
    }

    private record Mount(String source, String target, String type) {
        private static final String BIND_MOUNT_TYPE = "bind";

        public static Mount bind(String source, String target) {
            return new Mount(source, target, BIND_MOUNT_TYPE);
        }

        public static boolean isSameDirectory(String path, String otherPath) {
            return path.equals(otherPath) ||
                   path.equals(otherPath.substring(0, otherPath.length() - 1)) ||
                   path.substring(0, otherPath.length() - 1).equals(otherPath);
        }
    }
}

