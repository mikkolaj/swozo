package com.swozo.dto.course;

import com.swozo.dto.activity.CreateActivityRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CreateCourseRequest(
        @Schema(required = true) String name,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) int expectedStudentCount,
        @Schema(required = true) List<CreateActivityRequest> activityDetails,
        @Schema(required = true) List<String> studentEmails
) {
}
