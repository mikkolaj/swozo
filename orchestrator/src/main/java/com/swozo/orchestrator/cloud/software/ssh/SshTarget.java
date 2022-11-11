package com.swozo.orchestrator.cloud.software.ssh;

import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;

public record SshTarget(String ipAddress, int sshPort) {
    public static SshTarget from(VmResourceDetails vmResourceDetails) {
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
