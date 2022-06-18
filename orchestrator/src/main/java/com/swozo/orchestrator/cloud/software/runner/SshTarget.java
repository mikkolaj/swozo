package com.swozo.orchestrator.cloud.software.runner;

import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;

public record SshTarget(String ipAddress, int sshPort) {
    public static SshTarget from(VMResourceDetails vmResourceDetails) {
        return new SshTarget(
                vmResourceDetails.publicIpAddress(),
                vmResourceDetails.sshPort()
        );
    }

    @Override
    public String toString() {
        return String.format("%s:%s", ipAddress, sshPort);
    }
}
