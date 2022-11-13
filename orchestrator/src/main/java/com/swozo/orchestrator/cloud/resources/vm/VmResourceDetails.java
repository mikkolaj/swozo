package com.swozo.orchestrator.cloud.resources.vm;

public record VmResourceDetails(
        long internalResourceId,
        String publicIpAddress,
        String sshUser,
        int sshPort,
        String sshKeyPath) {
}
