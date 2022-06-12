package com.swozo.orchestrator.cloud.resources.vm;

public record VMResourceDetails(
        int internalResourceId,
        String publicIpAddress,
        String sshUser,
        int sshPort,
        String sshKeyPath) {
}
