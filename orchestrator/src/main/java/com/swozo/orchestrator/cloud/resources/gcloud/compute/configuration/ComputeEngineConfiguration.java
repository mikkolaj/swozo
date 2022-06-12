package com.swozo.orchestrator.cloud.resources.gcloud.compute.configuration;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudTimedVMProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudVMLifecycleManager;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.DefaultInstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.MachineTypeProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.OneToOneNATProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DefaultDebianDiskProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DiskProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.configuration.GcpConfiguration;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.configuration.CloudProviderNames;
import com.swozo.orchestrator.configuration.EnvNames;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = EnvNames.CLOUD_PROVIDER, havingValue = CloudProviderNames.GCLOUD)
public class ComputeEngineConfiguration {

    private final GcpConfiguration configuration;

    @Autowired
    public ComputeEngineConfiguration(GcpConfiguration configuration) {
        this.configuration = configuration;
    }

    @Bean
    InstanceProvider instanceProvider() {
        return new DefaultInstanceProvider(new MachineTypeProvider());
    }

    @Bean
    NetworkInterfaceProvider networkInterfaceProvider() {
        return new OneToOneNATProvider();
    }

    @Bean
    DiskProvider diskProvider() {
        return new DefaultDebianDiskProvider();
    }

    @Bean
    GCloudVMLifecycleManager gCloudVMLifecycleManager() {
        return new GCloudVMLifecycleManager(
                diskProvider(),
                networkInterfaceProvider(),
                instanceProvider(),
                configuration.requestTimeoutMinutes
        );
    }

    @Bean
    TimedVMProvider gCloudTimedVMProvider() {
        return new GCloudTimedVMProvider(
                configuration.project,
                configuration.zone,
                configuration.computeImageFamily,
                configuration.sshUser,
                configuration.sshKeyPath,
                gCloudVMLifecycleManager()
        );
    }
}
