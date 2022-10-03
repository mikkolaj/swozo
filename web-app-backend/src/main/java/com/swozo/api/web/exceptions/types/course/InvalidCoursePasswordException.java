package com.swozo.api.web.exceptions.types.course;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class InvalidCoursePasswordException extends ApiException {
    public InvalidCoursePasswordException() {
        super("Invalid password", ErrorType.INVALID_COURSE_PASSWORD);
    }
}
