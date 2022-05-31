package com.swozo.orchestrator.gcloud.compute.configuration;

import com.swozo.orchestrator.gcloud.compute.VMLifecycleManager;
import com.swozo.orchestrator.gcloud.compute.providers.instance.MachineTypeProvider;
import com.swozo.orchestrator.gcloud.compute.providers.instance.DefaultInstanceProvider;
import com.swozo.orchestrator.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.gcloud.compute.providers.networking.OneToOneNATProvider;
import com.swozo.orchestrator.gcloud.compute.providers.storage.DefaultDebianDiskProvider;
import com.swozo.orchestrator.gcloud.compute.providers.storage.DiskProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
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
