package com.swozo.dto.servicemodule;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ServiceModuleDetailsDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) String instructionsFromTechnicalTeacher,
        @Schema(required = true) String creatorName,
        @Schema(required = true) String subject,
        @Schema(required = true) LocalDateTime creationTime
) {
}
