package com.swozo.orchestrator.cloud.resources;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudVMProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudVMLifecycleManager;
import com.swozo.orchestrator.cloud.resources.vm.VMProvider;
import com.swozo.orchestrator.configuration.EnvNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudProviderConfiguration {
    private final String project;
    private final String zone;
    private final String imageFamily;
    private final GCloudVMLifecycleManager manager;

    public CloudProviderConfiguration(
            @Value("${" + EnvNames.GCP_PROJECT + "}") String project,
            @Value("${" + EnvNames.GCP_ZONE + "}") String zone,
            @Value("${" + EnvNames.GCP_VM_IMAGE_FAMILY + "}") String imageFamily,
            GCloudVMLifecycleManager manager) {
        this.imageFamily = imageFamily;
        this.manager = manager;
        this.project = project;
        this.zone = zone;
    }

    @Bean
    VMProvider instanceProvider() {
        return new GCloudVMProvider(project, zone, imageFamily, manager);
    }
}
