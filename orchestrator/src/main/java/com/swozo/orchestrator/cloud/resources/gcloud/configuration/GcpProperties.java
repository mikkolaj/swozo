package com.swozo.orchestrator.cloud.resources.gcloud.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;


@ConstructorBinding
@ConfigurationProperties(prefix = GcpProperties.prefix)
public record GcpProperties(
        String project,
        String zone,
        Compute compute,
        Ssh ssh
) {
    public static final String prefix = "gcp";

    private record Compute(int requestTimeoutMinutes, String imageFamily) {
    }

    private record Ssh(String user, String keyPath) {
    }

    public String sshUser() {
        return ssh.user;
    }

    public String sshKeyPath() {
        return ssh.keyPath;
    }

    public int computeRequestTimeoutMinutes() {
        return compute.requestTimeoutMinutes;
    }

    public String computeImageFamily() {
        return compute.imageFamily;
    }
}
