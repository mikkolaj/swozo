package com.swozo.api.web.exceptions.types.course;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class CourseNotFoundException extends ApiException {
    private CourseNotFoundException(String message) {
        super(message, ErrorType.COURSE_NOT_FOUND);
    }

    public static CourseNotFoundException withUUID(String joinUuid) {
        return new CourseNotFoundException("No such course with uuid: " + joinUuid);
    }

    public static CourseNotFoundException withId(Long id) {
        return new CourseNotFoundException("No course with id: " + id);
    }
}
