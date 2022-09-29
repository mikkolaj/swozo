package com.swozo.api.web.course.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record CourseSummaryDto(
        @Schema(required = true) String name,
        @Schema(required = true) boolean isPasswordProtected,
        @Schema(required = true) String joinUUID
) {
}
