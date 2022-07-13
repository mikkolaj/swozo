package com.swozo.dto.course;

import com.swozo.dto.activity.ActivityDetailsDto;
import com.swozo.dto.user.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record CourseDetailsDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) UserDetailsDto teacher,
        @Schema(required = true) LocalDateTime lastActivity,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) List<UserDetailsDto> students,
        @Schema(required = true) List<ActivityDetailsDto> activities
) {
}
