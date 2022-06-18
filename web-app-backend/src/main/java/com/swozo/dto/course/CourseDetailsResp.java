package com.swozo.dto.course;

import com.swozo.dto.activity.ActivityDetailsResp;
import com.swozo.dto.user.UserDetailsResp;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

public record CourseDetailsResp(
        @Schema(required = true) Long id,
        @Schema(required = true) String name,
        @Schema(required = true) UserDetailsResp teacher,
        @Schema(required = true) LocalDateTime lastActivity,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) List<UserDetailsResp> students,
        @Schema(required = true) List<ActivityDetailsResp> activities
) {
}
