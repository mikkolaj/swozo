package com.swozo.orchestrator.cloud.software.runner;

import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.cloud.software.ssh.SshAuth;
import com.swozo.orchestrator.cloud.software.ssh.SshTarget;

import java.util.Collection;
import java.util.List;

public record AnsibleConnectionDetails(Collection<SshTarget> targets, SshAuth auth) {
    public static AnsibleConnectionDetails from(VmResourceDetails vmResourceDetails) {
        return new AnsibleConnectionDetails(
                List.of(SshTarget.from(vmResourceDetails)),
                SshAuth.from(vmResourceDetails)
        );
    }
}
