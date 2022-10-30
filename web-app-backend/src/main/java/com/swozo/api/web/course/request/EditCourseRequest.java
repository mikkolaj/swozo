package com.swozo.api.web.course.request;

import java.util.Optional;

public record EditCourseRequest(
        Optional<String> name,
        Optional<String> subject,
        Optional<String> description,
        Optional<String> password,
        Optional<Boolean> isPublic
) {
}
