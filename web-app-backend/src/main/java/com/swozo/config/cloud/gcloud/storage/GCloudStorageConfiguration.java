package com.swozo.config.cloud.gcloud.storage;

import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.swozo.config.conditions.GCloudStorageCondition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(GCloudStorageCondition.class)
public class GCloudStorageConfiguration {
    @Bean
    Storage configureStorage() {
        return StorageOptions.getDefaultInstance().getService();
    }
}
