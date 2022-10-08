package com.swozo.api.common.files.storage.gcloud;

import com.google.cloud.storage.*;
import com.google.common.net.HttpHeaders;
import com.swozo.api.common.files.request.StorageAccessRequest;
import com.swozo.api.common.files.storage.StorageProvider;
import com.swozo.config.CloudProvider;
import com.swozo.config.cloud.gcloud.storage.GCloudStorageProperties;
import com.swozo.config.conditions.GCloudStorageCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Conditional(GCloudStorageCondition.class)
public class GCloudStorageProvider implements StorageProvider {
    private static final String SIZE_VALIDATION_HEADER = "X-Goog-Content-Length-Range";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final GCloudStorageProperties properties;
    private final Storage storage;

    @Autowired
    public GCloudStorageProvider(GCloudStorageProperties properties, Storage storage) {
        this.properties = properties;
        this.storage = storage;
        initBuckets();
    }

    private void initBuckets() {
        var bucketName = properties.webBucket().name();
        var bucket = Optional.ofNullable(storage.get(bucketName)).orElseGet(() -> {
            logger.warn("Bucket: " + bucketName + " not found, trying to create it");
            return createBucket(bucketName);
        });

        logger.debug("updating bucket CORS settings");
        var cors = Cors.newBuilder()
                        .setOrigins(Arrays.stream(properties.webBucket().corsAllowedOrigins()).map(Cors.Origin::of).toList())
                        .setMethods(List.of(HttpMethod.GET, HttpMethod.PUT, HttpMethod.POST, HttpMethod.OPTIONS))
                        .setResponseHeaders(List.of(SIZE_VALIDATION_HEADER, HttpHeaders.CONTENT_LANGUAGE))
                        .build();

        bucket.toBuilder()
                .setCors(List.of(cors))
                .build()
                .update();

        logger.info("Bucket: " + bucketName + " initialized successfully");
    }

    @Override
    public StorageAccessRequest createAuthorizedUploadRequest(String storageObjectName, long maxFileSizeBytes) {
        var extensionHeaders = Map.of(
                SIZE_VALIDATION_HEADER, String.format("%d,%d", 0, maxFileSizeBytes)
        );

        return buildStorageAccessRequest(
                properties.webBucket().name(),
                storageObjectName,
                Duration.ofMinutes(properties.uploadUrlExpirationMinutes()),
                HttpMethod.PUT,
                extensionHeaders
        );
    }

    @Override
    public StorageAccessRequest createAuthorizedDownloadRequest(String storageObjectName) {
        return buildStorageAccessRequest(
                properties.webBucket().name(),
                storageObjectName,
                Duration.ofMinutes(properties.downloadUrlExpirationMinutes()),
                HttpMethod.GET,
                Map.of()
        );
    }

    @Override
    public boolean isValid(StorageAccessRequest storageAccessRequest) {
        // TODO: verify signature
        return true;
    }

    @Override
    public CloudProvider getProviderType() {
        return CloudProvider.GCLOUD;
    }

    @Override
    public CompletableFuture<Void> cleanup(String storageObjectName) {
        // TODO
        return CompletableFuture.completedFuture(null);
    }

    private Bucket createBucket(String bucketName) {
        var createdBucket = storage.create(BucketInfo.of(bucketName));
        logger.info("Bucket created successfully");
        return createdBucket;
    }

    private URL buildSignedUrl(
            BlobInfo blobInfo,
            Duration validityDuration,
            HttpMethod method,
            Map<String, String> extensionHeaders
    ) {
        var signUrlOptions = new LinkedList<Storage.SignUrlOption>();

        signUrlOptions.add(Storage.SignUrlOption.httpMethod(method));
        signUrlOptions.add(Storage.SignUrlOption.withV4Signature());
        if (!extensionHeaders.isEmpty()) {
            signUrlOptions.add(Storage.SignUrlOption.withExtHeaders(extensionHeaders));
        }

        return storage.signUrl(
                blobInfo,
                validityDuration.toSeconds(), TimeUnit.SECONDS,
                signUrlOptions.toArray(Storage.SignUrlOption[]::new)
        );
    }

    private StorageAccessRequest buildStorageAccessRequest(
            String bucketName,
            String storageObjectName,
            Duration validityDuration,
            HttpMethod method,
            Map<String, String> headers
    ) {
        var blobInfo = BlobInfo.newBuilder(BlobId.of(bucketName, storageObjectName)).build();
        var url = buildSignedUrl(blobInfo, validityDuration, method, headers);

        return new StorageAccessRequest(
                getProviderType(),
                storageObjectName,
                url.toExternalForm(),
                LocalDateTime.now().plus(validityDuration),
                method.name(),
                headers
        );
    }
}
