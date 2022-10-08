package com.swozo.config.cloud.gcloud.storage;

import com.swozo.config.conditions.GCloudStorageCondition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Conditional;

@ConstructorBinding
@ConfigurationProperties(prefix = "gcp")
@Conditional(GCloudStorageCondition.class)
public record GCloudStorageProperties(
        String project,
        String zone,
        WebBucket webBucket,
        Long uploadUrlExpirationMinutes,
        Long downloadUrlExpirationMinutes
) {
    public record WebBucket(String name, String[] corsAllowedOrigins) {}
}