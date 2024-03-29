package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.IsolationMode;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.control.helpers.AbortHandler;
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
import net.bytebuddy.utility.RandomString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

import static com.swozo.utils.LoggingUtils.logIfSuccess;

@Service
@RequiredArgsConstructor
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final ServiceTypeEntity SUPPORTED_SCHEDULE = ServiceTypeEntity.JUPYTER;
    private static final String WORKDIR = "/home/swozo/jupyter";
    private static final int PROVISIONING_SECONDS = 500;
    private static final int MINUTES_FACTOR = 60;
    private static final String JUPYTER_PORT = "80";
    private static final String NOTEBOOK_PASSWORD_VARIABLE = "notebook_password";
    private static final String LAB_FILE_PATH = "/home/swozo/jupyter/lab_file.ipynb";
    private static final String SWOZO_USER = "swozo";
    private final TranslationsProvider translationsProvider;
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final BucketHandler bucketHandler;
    private final AbortHandler abortHandler;
    private final BackendRequestSender requestSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(
                "Jupyter Notebook",
                SUPPORTED_SCHEDULE.toString(),
                JupyterParameters.getParameterDescriptions(translationsProvider),
                Set.of(IsolationMode.ISOLATED),
                JupyterParameters.getConfigurationInstruction(translationsProvider),
                JupyterParameters.getUsageInstruction(translationsProvider),
                PROVISIONING_SECONDS
        );
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> provision(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails resource
    ) {
        var password = RandomString.make(5);
        logger.info("Password for request [id: {}]: {}", requestEntity.getId(), password);
        return CompletableFuture.runAsync(() -> {
                    logger.info("Started provisioning Jupyter on: {}", resource);
                    runPlaybook(resource, password);
                    abortHandler.abortIfNecessary(description.getId());
                }).thenCompose(x -> handleParameters(description.getDynamicProperties(), resource))
                .whenComplete(logIfSuccess(logger, provisioningComplete(resource)))
                .whenComplete(this::wrapExceptions)
                .thenCompose(x -> createLinks(requestEntity, description, resource, password));
    }

    private static String provisioningComplete(VmResourceDetails resource) {
        return String.format("Successfully provisioned Jupyter on resource: %s {}", resource);
    }

    private void wrapExceptions(Void unused, Throwable throwable) {
        if (throwable instanceof InvalidParametersException || throwable instanceof PlaybookFailed) {
            throw new ProvisioningFailed(throwable);
        }
    }

    public CompletableFuture<List<ActivityLinkInfo>> createLinks(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            VmResourceDetails vmResourceDetails,
            String password
    ) {
        var formattedLink = linkFormatter.getHttpLink(vmResourceDetails.publicIpAddress(), JUPYTER_PORT);
        return requestSender.getUserData(description.getActivityModuleId(), requestEntity.getId())
                .thenApply(users ->
                        users.stream().map(OrchestratorUserDto::id).map(createLink(formattedLink, password)).toList()
                );
    }

    private Function<Long, ActivityLinkInfo> createLink(String link, String password) {
        return userId -> new ActivityLinkInfo(userId, link, translationsProvider.t(
                "services.jupyter.instructions.connection",
                Map.of("password", password)
        ));
    }

    @Override
    public void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException {
        JupyterParameters.from(dynamicParameters);
    }

    @Override
    public ServiceTypeEntity getServiceType() {
        return SUPPORTED_SCHEDULE;
    }

    @Override
    public int getProvisioningSeconds(Map<String, String> dynamicParameters) {
        return PROVISIONING_SECONDS;
    }

    @Override
    public Optional<String> getWorkdirToSave(Map<String, String> dynamicParameters) {
        return Optional.of(WORKDIR);
    }

    private void runPlaybook(VmResourceDetails resource, String password) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                Playbook.PROVISION_JUPYTER,
                List.of(ansibleRunner.createUserVar(NOTEBOOK_PASSWORD_VARIABLE, password)),
                PROVISIONING_SECONDS / MINUTES_FACTOR
        );
    }

    private CompletableFuture<Void> handleParameters(Map<String, String> dynamicParameters, VmResourceDetails resource) {
        logger.info("Start handling parameters for {}", resource);
        var jupyterParameters = JupyterParameters.from(dynamicParameters);
        return bucketHandler.downloadToHost(resource, jupyterParameters.notebookLocation(), LAB_FILE_PATH, SWOZO_USER)
                .whenComplete(LoggingUtils.log(
                        logger,
                        String.format("Done downloading file for %s", resource),
                        String.format("Failed to download file for %s", resource)
                )).thenApply((x) -> {
                    // TODO file permissions as param
                    try {
                        ansibleRunner.runPlaybook(
                                AnsibleConnectionDetails.from(resource),
                                Playbook.EXECUTE_COMMAND,
                                List.of(ansibleRunner.createUserVar("command", "chmod 0666 " + LAB_FILE_PATH)),
                                1
                        );
                    } catch (Exception ignored) {}
                    return x;
                });
    }
}
