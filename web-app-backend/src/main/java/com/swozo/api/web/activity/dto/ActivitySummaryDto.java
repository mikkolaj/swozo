package com.swozo.api.web.activity.dto;

import com.swozo.api.web.user.dto.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record ActivitySummaryDto(
        @Schema(required = true) Long id,
        @Schema(required = true) Long courseId,
        @Schema(required = true) String name,
        @Schema(required = true) String courseName,
        @Schema(required = true) UserDetailsDto teacher,
        @Schema(required = true) LocalDateTime startTime,
        @Schema(required = true) LocalDateTime endTime,
        @Schema(required = true) boolean cancelled
){
}
