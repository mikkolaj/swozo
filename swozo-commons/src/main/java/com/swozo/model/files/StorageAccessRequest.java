package com.swozo.model.files;

import com.swozo.config.CloudProvider;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.Map;

public record StorageAccessRequest(
        @Schema(required = true) CloudProvider provider,
        @Schema(required = true) String filePath,
        @Schema(required = true) String signedUrl,
        @Schema(required = true) LocalDateTime validTo,
        @Schema(required = true) String httpMethod,
        @Schema(required = true) Map<String, String> httpHeaders
) {
}
