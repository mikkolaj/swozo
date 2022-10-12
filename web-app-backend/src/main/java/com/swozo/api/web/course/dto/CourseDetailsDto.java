package com.swozo.api.web.course.dto;

import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.user.dto.UserDetailsDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record CourseDetailsDto(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) UserDetailsDto teacher,
        @Schema(required = true) LocalDateTime lastActivityTime,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) List<ParticipantDetailsDto> students,
        @Schema(required = true) List<ActivityDetailsDto> activities,
        @Schema(required = true) String joinUUID,
        Optional<String> coursePassword
) {
}
