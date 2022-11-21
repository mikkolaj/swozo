package com.swozo.api.common.files.storage.gcloud;

import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.HttpMethod;
import com.google.cloud.storage.Storage;
import com.swozo.api.common.files.storage.StorageProvider;
import com.swozo.config.CloudProvider;
import com.swozo.config.cloud.gcloud.storage.GCloudStorageCondition;
import com.swozo.model.files.StorageAccessRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Service
@Profile("!test")
@RequiredArgsConstructor
@Conditional(GCloudStorageCondition.class)
public class GCloudStorageProvider implements StorageProvider {
    public static final String SIZE_VALIDATION_HEADER = "X-Goog-Content-Length-Range";
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Storage storage;

    @Override
    public StorageAccessRequest createAuthorizedUploadRequest(
            String bucketName, String storageObjectName, long maxFileSizeBytes, Duration validity
    ) {
        var extensionHeaders = Map.of(
                SIZE_VALIDATION_HEADER, String.format("%d,%d", 0, maxFileSizeBytes)
        );

        return buildStorageAccessRequest(
                bucketName,
                storageObjectName,
                validity,
                HttpMethod.PUT,
                extensionHeaders
        );
    }

    @Override
    public StorageAccessRequest createAuthorizedDownloadRequest(
            String bucketName, String storageObjectName, Duration validity
    ) {
        return buildStorageAccessRequest(
                bucketName,
                storageObjectName,
                validity,
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
    public CompletableFuture<Void> cleanup(String bucketName, String storageObjectName) {
        logger.info("Deleting file {} from bucket {}", storageObjectName, bucketName);
        storage.delete(BlobId.of(bucketName, storageObjectName));
        logger.info("Successfully deleted file {} from bucket {}", storageObjectName, bucketName);
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
