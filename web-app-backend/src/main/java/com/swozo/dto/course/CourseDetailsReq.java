package com.swozo.dto.course;

import com.swozo.dto.activity.ActivityDetailsReq;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

public record CourseDetailsReq(
        @Schema(required = true) String name,
        @Schema(required = true) String subject,
        @Schema(required = true) String description,
        @Schema(required = true) int expectedStudentCount,
        @Schema(required = true) List<ActivityDetailsReq> activityDetailReqs,
        @Schema(required = true) List<String> studentEmails
) {
}
