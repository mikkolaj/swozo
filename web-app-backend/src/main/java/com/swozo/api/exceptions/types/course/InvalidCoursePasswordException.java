package com.swozo.api.exceptions.types.course;

import com.swozo.api.exceptions.ApiException;
import com.swozo.api.exceptions.ErrorType;

public class InvalidCoursePasswordException extends ApiException {
    public InvalidCoursePasswordException() {
        super("Invalid password", ErrorType.INVALID_COURSE_PASSWORD);
    }
}
