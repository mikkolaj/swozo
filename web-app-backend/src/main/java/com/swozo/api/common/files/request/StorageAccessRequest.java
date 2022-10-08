package com.swozo.api.common.files.request;

import com.swozo.api.common.files.storage.StorageProviderType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

public record StorageAccessRequest(
        @Schema(required = true) StorageProviderType provider,
        @Schema(required = true) String filePath,
        @Schema(required = true) String signedUrl,
        @Schema(required = true) LocalDateTime validTo,
        @Schema(required = true) String httpMethod,
        @Schema(required = true) Map<String, String> httpHeaders
) {
}
