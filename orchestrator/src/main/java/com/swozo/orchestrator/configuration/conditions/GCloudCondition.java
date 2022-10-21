package com.swozo.orchestrator.configuration.conditions;

import com.swozo.config.CloudProvider;
import com.swozo.config.CloudProviderCondition;

public class GCloudCondition extends CloudProviderCondition {
    @Override
    public String getProviderProperty() {
        return "cloud-provider";
    }

    @Override
    public CloudProvider getRequiredCloudProvider() {
        return CloudProvider.GCLOUD;
    }
}
