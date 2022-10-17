package com.swozo.api.web.servicemodule.dto;

import com.swozo.api.web.user.dto.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ServiceModuleDetailsDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) String instructionsFromTechnicalTeacher,
        @Schema(required = true) UserDetailsDto creator,
        @Schema(required = true) String subject,
        @Schema(required = true) LocalDateTime creationTime
) {
}
