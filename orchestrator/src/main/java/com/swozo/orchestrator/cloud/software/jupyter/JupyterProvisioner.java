package com.swozo.orchestrator.cloud.software.jupyter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.LinkFormatter;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleConnectionDetails;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.NotebookFailed;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import com.swozo.utils.StorageAccessRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.util.List;
import java.util.Map;

import static com.swozo.communication.http.RequestSender.unwrap;

@Service
@RequiredArgsConstructor
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final String VERSION = "1.0.0";
    private static final ScheduleType SUPPORTED_SCHEDULE = ScheduleType.JUPYTER;
    private static final int PROVISIONING_SECONDS = 600;
    private static final int MINUTES_FACTOR = 60;
    private static final String JUPYTER_PORT = "80";
    private static final String MAIN_LINK_DESCRIPTION = "Hasło: swozo123"; // TODO
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final ApplicationProperties properties;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final RequestSender requestSender;

    @Override
    public ServiceConfig getServiceConfig() {
        return new ServiceConfig(SUPPORTED_SCHEDULE.toString(), VERSION, JupyterParameters.getParameterDescriptions());
    }

    @Override
    // TODO: getting notebook from specified location
    public List<ActivityLinkInfo> provision(
            VMResourceDetails resource,
            Map<String, String> parameters,
            String provisionerVersion
    ) throws ProvisioningFailed {
        try {
            logger.info("Started provisioning Jupyter on: {}", resource);
            runPlaybook(resource);
            handleParameters(parameters, resource);
            logger.info("Successfully provisioned Jupyter on resource: {}", resource);
            return createLinks(resource);
        } catch (NotebookFailed e) {
            throw new ProvisioningFailed(e);
        }
    }

    @Override
    public void validateParameters(Map<String, String> dynamicParameters, String version) throws InvalidParametersException {
        assertSupportedVersion(version);
        JupyterParameters.from(dynamicParameters);
    }

    @Override
    public ScheduleType getScheduleType() {
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

    private List<ActivityLinkInfo> createLinks(VMResourceDetails vmResourceDetails) {
        var formattedLink = linkFormatter.getHttpLink(vmResourceDetails.publicIpAddress(), JUPYTER_PORT);
        return List.of(new ActivityLinkInfo(formattedLink, MAIN_LINK_DESCRIPTION));
    }

    private void assertSupportedVersion(String version) {
        if (!VERSION.equals(version)) {
            throw new IllegalArgumentException("Version: " + version + " for Jupyter provisioner is not supported");
        }
    }

    @SneakyThrows
    private void handleParameters(Map<String, String> parameters, VMResourceDetails resource) {
        // TODO do this properly
        var properties = JupyterParameters.from(parameters);
        var resp = unwrap(requestSender.sendGet(
                new URI("http://localhost:5000/files/" + properties.notebookLocation()), new TypeReference<StorageAccessRequest>() {
                })).join();
        var curlCmd = String.format("curl %s --output /home/swozo/jupyter/lab_file.ipynb\n", resp.signedUrl());
        System.out.println(resp);

        var tempPlaybookFile = File.createTempFile(properties.notebookLocation() + "_dl", "_download.yml");
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
