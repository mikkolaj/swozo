package com.swozo.api.common.files.storage.gcloud;

import com.google.cloud.storage.*;
import com.swozo.api.common.files.request.StorageAccessRequest;
import com.swozo.api.common.files.storage.StorageProvider;
import com.swozo.api.common.files.storage.StorageProviderType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class GCloudStorageProvider implements StorageProvider {
    private static final String BUCKET_NAME = "my-new-unique-bucket-with-random-chars-23e23709sdskntp3h28lfs8i";

    @Override
    public void setupRequirements() {
        System.out.println(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        var storage = StorageOptions.getDefaultInstance().getService();
//        var bucket = storage.create(BucketInfo.of(BUCKET_NAME));
//        var cors = Cors.newBuilder()
//                        .setOrigins(List.of(Cors.Origin.of("http://localhost:3000")))
//                        .setMethods(List.of(HttpMethod.GET, HttpMethod.PUT, HttpMethod.POST, HttpMethod.OPTIONS))
//                        .build();
//
//        bucket.toBuilder()
//                .setCors(List.of(cors))
//                .build()
//                .update();

        System.out.println("created");
    }

    @Override
    public StorageAccessRequest createAuthorizedUploadRequest(String storageObjectName, long maxFileSizeBytes) {
        var storage = StorageOptions.getDefaultInstance().getService();
        var blobInfo = BlobInfo.newBuilder(BlobId.of(BUCKET_NAME, storageObjectName)).build();
        var extensionHeaders = Map.of(
                "X-Goog-Content-Length-Range", String.format("%d,%d", 0, maxFileSizeBytes)
        );
        var url = storage.signUrl(blobInfo,
                5, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withExtHeaders(extensionHeaders),
                Storage.SignUrlOption.withV4Signature());
        System.out.println("GENERATED PUT SIGNED URL");
        System.out.println(url);

        return new StorageAccessRequest(
                getProviderType(),
                storageObjectName,
                url.toExternalForm(),
                LocalDateTime.now().plusMinutes(5),
                HttpMethod.PUT.name(),
                extensionHeaders
        );
    }

    @Override
    public StorageAccessRequest createAuthorizedDownloadRequest(String storageObjectName) {
        var storage = StorageOptions.getDefaultInstance().getService();
        var blobInfo = BlobInfo.newBuilder(BlobId.of(BUCKET_NAME, storageObjectName)).build();
        var url = storage.signUrl(blobInfo,
                2, TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.GET),
                Storage.SignUrlOption.withV4Signature());
        System.out.println("GENERATED GET SIGNED URL");
        System.out.println(url);

        return new StorageAccessRequest(
                getProviderType(),
                storageObjectName,
                url.toExternalForm(),
                LocalDateTime.now().plusMinutes(2),
                HttpMethod.GET.name(),
                Map.of()
            );
    }

    @Override
    public boolean isValid(StorageAccessRequest storageAccessRequest) {
        // TODO verify signature
        return true;
    }

    @Override
    public StorageProviderType getProviderType() {
        return StorageProviderType.GCLOUD;
    }

    @Override
    public void cleanup(String storageObjectName) {
        // TODO
    }
}
