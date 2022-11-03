package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.orchestrator.api.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.LinkFormatter;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.NotebookFailed;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final ServiceTypeEntity SUPPORTED_SCHEDULE = ServiceTypeEntity.JUPYTER;
    private static final int PROVISIONING_SECONDS = 600;
    private static final int MINUTES_FACTOR = 60;
    private static final String JUPYTER_PORT = "80";
    private static final String MAIN_LINK_DESCRIPTION = "swozo123"; // TODO
    private final TranslationsProvider translationsProvider;
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final ApplicationProperties properties;
    private final BackendRequestSender requestSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(SUPPORTED_SCHEDULE.toString(), JupyterParameters.getParameterDescriptions(translationsProvider));
    }

    @Override
    // TODO: getting notebook from specified location
    public List<ActivityLinkInfo> provision(VMResourceDetails resource, Map<String, String> parameters) throws ProvisioningFailed {
        try {
            logger.info("Started provisioning Jupyter on: {}", resource);
            runPlaybook(resource);
            handleParameters(parameters, resource);
            logger.info("Successfully provisioned Jupyter on resource: {}", resource);
            return createLinks(resource);
        } catch (InvalidParametersException | NotebookFailed e) {
            throw new ProvisioningFailed(e);
        }
    }

    @Override
    public List<ActivityLinkInfo> createLinks(VMResourceDetails vmResourceDetails) {
        var formattedLink = linkFormatter.getHttpLink(vmResourceDetails.publicIpAddress(), JUPYTER_PORT);
        return List.of(new ActivityLinkInfo(
                formattedLink,
                translationsProvider.t(
                        "services.jupyter.connectionInstruction",
                        Map.of("password", MAIN_LINK_DESCRIPTION)
                )
        ));
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

    private void runPlaybook(VMResourceDetails resource) {
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                properties.jupyterPlaybookPath(),
                PROVISIONING_SECONDS / MINUTES_FACTOR
        );
    }

    @SneakyThrows
    private void handleParameters(Map<String, String> dynamicParameters, VMResourceDetails resource) {
        // TODO do this properly
        logger.info("Start handling parameters for {}", resource);
        var jupyterParameters = JupyterParameters.from(dynamicParameters);
        var resp = CheckedExceptionConverter.from(
                () -> requestSender.getSignedDownloadUrl(jupyterParameters.notebookLocation()).get(),
                ProvisioningFailed::new
        ).get();
        logger.info("Got resp for {}", resource);
        var curlCmd = String.format("curl %s --output /home/swozo/jupyter/lab_file.ipynb\n", resp.signedUrl());
        System.out.println(resp);

        var tempPlaybookFile = File.createTempFile(jupyterParameters.notebookLocation() + "_dl", "_download.yml");
        try (var writer = new FileWriter(tempPlaybookFile)) {
            writer.write("- name: Handle dynamic params\n" +
                    "  hosts: all\n" +
                    "  tasks:\n" +
                    "    - name: download notebook\n" +
                    "      command: " + curlCmd
            );
        }

        System.out.println("downloading file");
        ansibleRunner.runPlaybook(
                AnsibleConnectionDetails.from(resource),
                tempPlaybookFile.getPath(),
                5
        );
        System.out.println("done");

        var x = tempPlaybookFile.delete();
        System.out.println("remove file: " + x);
    }
}
