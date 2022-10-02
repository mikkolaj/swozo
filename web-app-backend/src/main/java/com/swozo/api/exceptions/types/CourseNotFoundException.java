package com.swozo.api.exceptions.types;

import com.swozo.api.exceptions.ApiException;
import com.swozo.api.exceptions.ErrorType;

public class CourseNotFoundException extends ApiException {
    private CourseNotFoundException(String message) {
        super(message, ErrorType.COURSE_NOT_FOUND);
    }

    public static CourseNotFoundException withUuid(String joinUuid) {
        return new CourseNotFoundException("No such course with uuid: " + joinUuid);
    }

    public static CourseNotFoundException withId(Long id) {
        return new CourseNotFoundException("No course with id: " + id);
    }
}
