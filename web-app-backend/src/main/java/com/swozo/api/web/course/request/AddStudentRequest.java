package com.swozo.api.web.course.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record AddStudentRequest(
        @Schema(required = true) String email
) {
}
