package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.model.links.Link;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.HttpLinkFormatter;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.runner.AnsibleRunner;
import com.swozo.orchestrator.cloud.software.runner.SshTarget;
import com.swozo.orchestrator.configuration.ApplicationProperties;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final int PROVISIONING_SECONDS = 240;
    private static final String JUPYTER_PORT = "8888";
    private static final String MAIN_LINK_DESCRIPTION = "Main page.";
    private final AnsibleRunner ansibleRunner;
    private final String playbookPath;

    public JupyterProvisioner(AnsibleRunner ansibleRunner, ApplicationProperties properties) {
        this.ansibleRunner = ansibleRunner;
        this.playbookPath = properties.jupyterPlaybookPath();
    }

    @Override
    public List<Link> provision(VMResourceDetails resource) throws InterruptedException {
        var targets = List.of(SshTarget.from(resource));
        var result = ansibleRunner.runNotebook(targets, resource.sshUser(), resource.sshKeyPath(), playbookPath);
        if (result.returnCode() == 0) {
            return createLinks(resource);
        } else {
            return Collections.emptyList();
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
