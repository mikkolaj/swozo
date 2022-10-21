package com.swozo.config.cloud.gcloud.storage;

import com.swozo.config.CloudProvider;
import com.swozo.config.CloudProviderCondition;

public class GCloudStorageCondition extends CloudProviderCondition {
    @Override
    public String getProviderProperty() {
        return "storage.provider";
    }

    @Override
    public CloudProvider getRequiredCloudProvider() {
        return CloudProvider.GCLOUD;
    }
}
