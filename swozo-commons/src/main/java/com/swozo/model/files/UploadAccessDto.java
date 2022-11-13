package com.swozo.model.files;

import io.swagger.v3.oas.annotations.media.Schema;

public record UploadAccessDto(
        @Schema(required = true) InitFileUploadRequest initFileUploadRequest,
        @Schema(required = true) StorageAccessRequest storageAccessRequest
) {
}
