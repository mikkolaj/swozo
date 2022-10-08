package com.swozo.config.conditions;

import com.swozo.config.CloudProvider;
import com.swozo.config.CloudProviderCondition;

public class GCloudStorageCondition extends CloudProviderCondition {
    @Override
    public String getProviderProperty() {
        return "storage.provider";
    }

    @Override
    public CloudProvider getCloudProvider() {
        return CloudProvider.GCLOUD;
    }
}
