package com.swozo.api.web.course.dto;

import com.swozo.api.web.user.dto.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record CourseSummaryDto(
        @Schema(required = true) String name,
        @Schema(required = true) String description,
        @Schema(required = true) String subject,
        @Schema(required = true) LocalDateTime creationTime,
        @Schema(required = true) UserDetailsDto teacher,
        @Schema(required = true) boolean isPasswordProtected,
        @Schema(required = true) String joinUUID
) {
}
