package com.swozo.orchestrator.cloud.resources.vm;

public record VMConnectionDetails(
        int internalResourceId,
        String publicIpAddress,
        String sshUser,
        String sshKeyPath) {
}
