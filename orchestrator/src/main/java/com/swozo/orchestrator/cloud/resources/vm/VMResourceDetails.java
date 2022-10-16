package com.swozo.orchestrator.cloud.resources.vm;

public record VMResourceDetails(
        long internalResourceId,
        String publicIpAddress,
        String sshUser,
        int sshPort,
        String sshKeyPath) {
}
