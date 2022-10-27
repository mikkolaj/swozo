package com.swozo.orchestrator.cloud.software.ssh;

import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;

public record SshAuth(String sshUser, String sshKeyPath) {
    public static SshAuth from(VMResourceDetails vmResourceDetails) {
        return new SshAuth(
                vmResourceDetails.sshUser(),
                vmResourceDetails.sshKeyPath()
        );
    }
}
