package com.swozo.api.common.files.dto;

import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.model.utils.StorageAccessRequest;
import io.swagger.v3.oas.annotations.media.Schema;

public record UploadAccessDto(
        @Schema(required = true) InitFileUploadRequest initFileUploadRequest,
        @Schema(required = true) StorageAccessRequest storageAccessRequest
) {
}
