package com.swozo.config.properties;

import com.swozo.config.CloudProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@ConstructorBinding
@ConfigurationProperties(prefix = "storage")
public record StorageProperties(CloudProvider provider,
                                Duration internalDownloadValidity,
                                Duration internalUploadValidity,
                                Duration externalDownloadValidity,
                                Duration externalUploadValidity,
                                WebBucket webBucket
) {
    public record WebBucket(String name, String[] corsAllowedOrigins) {}
}
