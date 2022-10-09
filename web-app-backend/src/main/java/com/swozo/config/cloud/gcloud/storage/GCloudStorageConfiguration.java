package com.swozo.config.cloud.gcloud.storage;

import com.google.cloud.storage.*;
import com.google.common.net.HttpHeaders;
import com.swozo.config.conditions.GCloudStorageCondition;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static com.swozo.api.common.files.storage.gcloud.GCloudStorageProvider.SIZE_VALIDATION_HEADER;

@Configuration
@RequiredArgsConstructor
@Conditional(GCloudStorageCondition.class)
public class GCloudStorageConfiguration {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final GCloudStorageProperties properties;

    @Bean
    public Storage configureStorage() {
        var storage = StorageOptions.getDefaultInstance().getService();
        configureBuckets(storage);
        return storage;
    }

    private void configureBuckets(Storage storage) {
        var bucketName = properties.webBucket().name();
        var bucket = getOrCreateBucket(storage, bucketName);

        logger.debug("updating bucket CORS settings");
        var cors = Cors.newBuilder()
                .setOrigins(Arrays.stream(properties.webBucket().corsAllowedOrigins()).map(Cors.Origin::of).toList())
                .setMethods(List.of(HttpMethod.GET, HttpMethod.PUT, HttpMethod.POST, HttpMethod.OPTIONS))
                .setResponseHeaders(List.of(SIZE_VALIDATION_HEADER, HttpHeaders.CONTENT_TYPE))
                .build();

        bucket.toBuilder()
                .setCors(List.of(cors))
                .build()
                .update();

        logger.info("Bucket: " + bucketName + " initialized successfully");
    }

    private Bucket getOrCreateBucket(Storage storage, String bucketName) {
        return Optional.ofNullable(storage.get(bucketName)).orElseGet(() -> {
            logger.warn("Bucket: " + bucketName + " not found, trying to create it");
            var createdBucket = storage.create(BucketInfo.of(bucketName));
            logger.info("Bucket created successfully");
            return createdBucket;
        });
    }
}
