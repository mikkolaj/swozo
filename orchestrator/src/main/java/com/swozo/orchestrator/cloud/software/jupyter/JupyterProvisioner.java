package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.model.links.Link;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.HttpLinkFormatter;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.SshTarget;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final int PROVISIONING_SECONDS = 240;
    private static final String JUPYTER_PORT = "80";
    private static final String MAIN_LINK_DESCRIPTION = "Main page.";
    private final AnsibleRunner ansibleRunner;
    private final String playbookPath;
    private final Logger logger;

    @Autowired
    public JupyterProvisioner(AnsibleRunner ansibleRunner, ApplicationProperties properties) {
        this.ansibleRunner = ansibleRunner;
        this.playbookPath = properties.jupyterPlaybookPath();
        this.logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public List<Link> provision(VMResourceDetails resource) throws InterruptedException, ProvisioningFailed {
        logger.info("Started provisioning Jupyter on: {}", resource);
        var targets = List.of(SshTarget.from(resource));
        var result = ansibleRunner.runNotebook(targets, resource.sshUser(), resource.sshKeyPath(), playbookPath);
        if (result.returnCode() == 0) {
            logger.info("Successfully provisioned Jupyter on resource: {}", resource);
            return createLinks(resource);
        } else {
            throw new ProvisioningFailed("Failed to provision Jupyter");
        }
    }

    @Override
    public int getProvisioningSeconds() {
        return PROVISIONING_SECONDS;
    }

    private List<Link> createLinks(VMResourceDetails vmResourceDetails) {
        var formattedLink = HttpLinkFormatter.getLink(vmResourceDetails.publicIpAddress(), JUPYTER_PORT);
        return List.of(new Link(formattedLink, MAIN_LINK_DESCRIPTION));
    }
}
