package com.swozo.api.common.files.storage.gcloud;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.swozo.api.common.files.request.StorageAccessRequest;
import com.swozo.api.common.files.storage.StorageProvider;
import com.swozo.config.CloudProvider;
import com.swozo.config.cloud.gcloud.storage.GCloudStorageProperties;
import com.swozo.config.conditions.GCloudStorageCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Conditional(GCloudStorageCondition.class)
public class GCloudStorageProvider implements StorageProvider {
    public static final String SIZE_VALIDATION_HEADER = "X-Goog-Content-Length-Range";

    private final GCloudStorageProperties properties;
    private final Storage storage;

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
