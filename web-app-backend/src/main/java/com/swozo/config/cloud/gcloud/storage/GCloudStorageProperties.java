package com.swozo.config.cloud.gcloud.storage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Conditional;

@ConstructorBinding
@ConfigurationProperties(prefix = "gcp.storage")
@Conditional(GCloudStorageCondition.class)
public record GCloudStorageProperties(
        String project,
        String zone
) {
}
