package com.swozo.api.web.course.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record JoinCourseRequest(
        @Schema(required = true) String joinUUID,
        @Schema(required = false) String password
) {
}
