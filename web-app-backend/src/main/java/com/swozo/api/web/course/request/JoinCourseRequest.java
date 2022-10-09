package com.swozo.api.web.course.request;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Optional;

public record JoinCourseRequest(
        @Schema(required = true) String joinUUID,
        Optional<String> password
) {
}
