package com.swozo.api.common.files.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record FileDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) Long sizeBytes,
        @Schema(required = true) LocalDateTime createdAt
) {
}
