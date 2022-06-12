package com.swozo.orchestrator.cloud.resources.gcloud;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudTimedVMProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudVMLifecycleManager;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DiskProvider;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.configuration.CloudProviderNames;
import com.swozo.orchestrator.configuration.EnvNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
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
    private final InstanceProvider instanceProvider;
    private final NetworkInterfaceProvider networkInterfaceProvider;
    private final DiskProvider diskProvider;

    public GcpConfiguration(
            @Value("${" + GcpEnvNames.GCP_PROJECT + "}") String project,
            @Value("${" + GcpEnvNames.GCP_ZONE + "}") String zone,
            @Value("${" + GcpEnvNames.GCP_COMPUTE_IMAGE_FAMILY + "}") String computeImageFamily,
            @Value("${" + GcpEnvNames.GCP_REQUEST_TIMEOUT + "}") int requestTimeoutMinutes,
            @Value("${" + GcpEnvNames.GCP_SSH_USER + "}") String sshUser,
            @Value("${" + GcpEnvNames.GCP_SSH_KEY_PATH + "}") String sshKeyPath,
            DiskProvider diskProvider,
            NetworkInterfaceProvider networkInterfaceProvider,
            InstanceProvider instanceProvider

    ) {
        this.project = project;
        this.zone = zone;
        this.computeImageFamily = computeImageFamily;
        this.requestTimeoutMinutes = requestTimeoutMinutes;
        this.sshUser = sshUser;
        this.sshKeyPath = sshKeyPath;
        this.diskProvider = diskProvider;
        this.networkInterfaceProvider = networkInterfaceProvider;
        this.instanceProvider = instanceProvider;
    }

    @Bean
    GCloudVMLifecycleManager gCloudVMLifecycleManager() {
        return new GCloudVMLifecycleManager(
                diskProvider,
                networkInterfaceProvider,
                instanceProvider,
                requestTimeoutMinutes
        );
    }

    @Bean
    TimedVMProvider gCloudTimedVMProvider() {
        return new GCloudTimedVMProvider(
                project,
                zone,
                computeImageFamily,
                sshUser,
                sshKeyPath,
                gCloudVMLifecycleManager()
        );
    }
}
