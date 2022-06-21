package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.model.links.Link;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.LinkFormatter;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.NotebookFailed;
import com.swozo.orchestrator.cloud.software.runner.SshTarget;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final int PROVISIONING_SECONDS = 600;
    private static final int MINUTES_FACTOR = 60;
    private static final String JUPYTER_PORT = "80";
    private static final String MAIN_LINK_DESCRIPTION = "Hasło: swozo123";
    private final AnsibleRunner ansibleRunner;
    private final LinkFormatter linkFormatter;
    private final String playbookPath;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public JupyterProvisioner(AnsibleRunner ansibleRunner, LinkFormatter linkFormatter, ApplicationProperties properties) {
        this.ansibleRunner = ansibleRunner;
        this.linkFormatter = linkFormatter;
        this.playbookPath = properties.jupyterPlaybookPath();
    }

    @Override
    public List<Link> provision(VMResourceDetails resource) throws InterruptedException, ProvisioningFailed {
        try {
            logger.info("Started provisioning Jupyter on: {}", resource);
            var targets = List.of(SshTarget.from(resource));
            ansibleRunner.runNotebook(targets, resource.sshUser(), resource.sshKeyPath(), playbookPath, PROVISIONING_SECONDS / MINUTES_FACTOR);
            logger.info("Successfully provisioned Jupyter on resource: {}", resource);
            return createLinks(resource);
        } catch (NotebookFailed e) {
            throw new ProvisioningFailed(e);
        }
    }

    @Override
    public int getProvisioningSeconds() {
        return PROVISIONING_SECONDS;
    }

    private List<Link> createLinks(VMResourceDetails vmResourceDetails) {
        var formattedLink = linkFormatter.getHttpLink(vmResourceDetails.publicIpAddress(), JUPYTER_PORT);
        return List.of(new Link(formattedLink, MAIN_LINK_DESCRIPTION));
    }
}
