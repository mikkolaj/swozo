package com.swozo.orchestrator.cloud.resources.gcloud.configuration;

import com.swozo.orchestrator.configuration.CloudProviderNames;
import com.swozo.orchestrator.configuration.EnvNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = EnvNames.CLOUD_PROVIDER, havingValue = CloudProviderNames.GCLOUD)
public class GcpConfiguration {
    public final String project;
    public final String zone;
    public final String computeImageFamily;
    public final int requestTimeoutMinutes;
    public final String sshUser;
    public final String sshKeyPath;

    public GcpConfiguration(
            @Value("${" + GcpEnvNames.GCP_PROJECT + "}") String project,
            @Value("${" + GcpEnvNames.GCP_ZONE + "}") String zone,
            @Value("${" + GcpEnvNames.GCP_COMPUTE_IMAGE_FAMILY + "}") String computeImageFamily,
            @Value("${" + GcpEnvNames.GCP_REQUEST_TIMEOUT + "}") int requestTimeoutMinutes,
            @Value("${" + GcpEnvNames.GCP_SSH_USER + "}") String sshUser,
            @Value("${" + GcpEnvNames.GCP_SSH_KEY_PATH + "}") String sshKeyPath
    ) {
        this.project = project;
        this.zone = zone;
        this.computeImageFamily = computeImageFamily;
        this.requestTimeoutMinutes = requestTimeoutMinutes;
        this.sshUser = sshUser;
        this.sshKeyPath = sshKeyPath;
    }

}
