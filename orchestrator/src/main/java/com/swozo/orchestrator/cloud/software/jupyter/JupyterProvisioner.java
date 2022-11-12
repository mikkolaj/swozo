package com.swozo.orchestrator.cloud.software.jupyter;

import com.google.common.base.Functions;
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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final ServiceTypeEntity SUPPORTED_SCHEDULE = ServiceTypeEntity.JUPYTER;
    private static final String WORKDIR = "/home/swozo/jupyter";
    private static final int PROVISIONING_SECONDS = 600;
    private static final int MINUTES_FACTOR = 60;
    private static final String JUPYTER_PORT = "80";
    private static final String MAIN_LINK_DESCRIPTION = "swozo123"; // TODO
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
                Set.of(IsolationMode.ISOLATED)
        );
    }

    @Override
    public CompletableFuture<List<ActivityLinkInfo>> provision(VmResourceDetails resource, Map<String, String> parameters) {
        return CompletableFuture.runAsync(() -> {
                    logger.info("Started provisioning Jupyter on: {}", resource);
                    runPlaybook(resource);
                    handleParameters(parameters, resource);
                    logger.info("Successfully provisioned Jupyter on resource: {}", resource);
                }).whenComplete(this::wrapExceptions)
                .thenCompose(Functions.constant(createLinks(resource)));
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

    private void handleParameters(Map<String, String> dynamicParameters, VmResourceDetails resource) {
        logger.info("Start handling parameters for {}", resource);
        var jupyterParameters = JupyterParameters.from(dynamicParameters);
        bucketHandler.downloadToHost(resource, jupyterParameters.notebookLocation(), "/home/swozo/jupyter/lab_file.ipynb");
        logger.info("Done downloading file for {}", resource);
    }
}
