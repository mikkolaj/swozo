package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.IsolationMode;
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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.swozo.utils.LoggingUtils.logIfSuccess;

@Service
@RequiredArgsConstructor
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final ServiceTypeEntity SUPPORTED_SCHEDULE = ServiceTypeEntity.JUPYTER;
    private static final String WORKDIR = "/home/swozo/jupyter";
    private static final int PROVISIONING_SECONDS = 600;
    private static final int MINUTES_FACTOR = 60;
    private static final String JUPYTER_PORT = "80";
    private static final String MAIN_LINK_DESCRIPTION = "swozo123"; // TODO
    private static final String LAB_FILE_PATH = "/home/swozo/jupyter/lab_file.ipynb";
    private final TranslationsProvider translationsProvider;
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final BucketHandler bucketHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(
                SUPPORTED_SCHEDULE.toString(),
                JupyterParameters.getParameterDescriptions(translationsProvider),
                Set.of(IsolationMode.ISOLATED, IsolationMode.SHARED)
        );
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> provision(VmResourceDetails resource, Map<String, String> parameters) {
        return CompletableFuture.runAsync(() -> {
                    logger.info("Started provisioning Jupyter on: {}", resource);
                    runPlaybook(resource);
                }).thenCompose(x -> handleParameters(parameters, resource))
                .whenComplete(logIfSuccess(logger, provisioningComplete(resource)))
                .whenComplete(this::wrapExceptions)
                .thenCompose(x -> createLinks(resource));
    }

    private static String provisioningComplete(VmResourceDetails resource) {
        return String.format("Successfully provisioned Jupyter on resource: %s", resource);
    }

    private void wrapExceptions(Void unused, Throwable throwable) {
        if (throwable instanceof InvalidParametersException || throwable instanceof PlaybookFailed) {
            throw new ProvisioningFailed(throwable);
        }
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> createLinks(VmResourceDetails vmResourceDetails) {
        var formattedLink = linkFormatter.getHttpLink(vmResourceDetails.publicIpAddress(), JUPYTER_PORT);
        return CompletableFuture.completedFuture(List.of(new ActivityLinkInfo(
                1L, // TODO
                formattedLink,
                translationsProvider.t(
                        "services.jupyter.connectionInstruction",
                        Map.of("password", MAIN_LINK_DESCRIPTION)
                )
        )));
    }

    @Override
    public void validateParameters(Map<String, String> dynamicParameters) throws InvalidParametersException {
        JupyterParameters.from(dynamicParameters);
    }

    @Override
    public ServiceTypeEntity getScheduleType() {
        return SUPPORTED_SCHEDULE;
    }

    @Override
    public int getProvisioningSeconds() {
        return PROVISIONING_SECONDS;
    }

    @Override
    public Optional<String> getWorkdirToSave() {
        return Optional.of(WORKDIR);
    }

    private void runPlaybook(VmResourceDetails resource) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                Playbook.PROVISION_JUPYTER,
                PROVISIONING_SECONDS / MINUTES_FACTOR
        );
    }

    private CompletableFuture<Void> handleParameters(Map<String, String> dynamicParameters, VmResourceDetails resource) {
        logger.info("Start handling parameters for {}", resource);
        var jupyterParameters = JupyterParameters.from(dynamicParameters);
        return bucketHandler.downloadToHost(resource, jupyterParameters.notebookLocation(), LAB_FILE_PATH)
                .whenComplete(LoggingUtils.log(
                        logger,
                        String.format("Done downloading file for %s", resource),
                        String.format("Failed to download file for %s", resource)
                ));
    }
}
