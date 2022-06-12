package com.swozo.orchestrator.cloud.resources.gcloud.compute.configuration;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.DefaultInstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.MachineTypeProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.OneToOneNATProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DefaultDebianDiskProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DiskProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(name = "cloud.provider", havingValue = "gcloud")
public class ComputeEngineConfiguration {
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
}
