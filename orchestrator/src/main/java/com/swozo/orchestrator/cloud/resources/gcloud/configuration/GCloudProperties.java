package com.swozo.orchestrator.cloud.resources.gcloud.configuration;

import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Conditional;


@ConstructorBinding
@ConfigurationProperties(prefix = GCloudProperties.PREFIX)
@Conditional(GCloudCondition.class)
public record GCloudProperties(
        String project,
        String zone,
        Compute compute,
        Ssh ssh
) {
    public static final String PREFIX = "gcp";

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
