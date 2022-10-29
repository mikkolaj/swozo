package com.swozo.api.web.servicemodule.dto;

import com.swozo.api.web.user.dto.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ServiceModuleUsageDto(
        @Schema(required = true) UserDetailsDto courseCreator,
        @Schema(required = true) Long courseId,
        @Schema(required = true) String courseName,
        @Schema(required = true) String activityName,
        @Schema(required = true) LocalDateTime addedAt
) {
}
