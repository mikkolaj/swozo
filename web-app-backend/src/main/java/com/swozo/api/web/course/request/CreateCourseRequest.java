package com.swozo.api.web.course.request;

import com.swozo.api.web.activity.request.CreateActivityRequest;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Optional;

public record CreateCourseRequest(
        @Schema(required = true) String name,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) int expectedStudentCount,
        @Schema(required = true) boolean isPublic,
        @Schema(required = true) List<CreateActivityRequest> activities,
        Optional<String> password
) {
}
