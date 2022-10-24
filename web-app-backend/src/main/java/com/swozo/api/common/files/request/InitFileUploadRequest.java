package com.swozo.api.common.files.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record InitFileUploadRequest(
        @Schema(required = true) String filename,
        @Schema(required = true) long sizeBytes
) {
}
