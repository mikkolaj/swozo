package com.swozo.api.web.exceptions.types.course;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class NotAMemberException extends ApiException {
    public NotAMemberException(String message) {
        super(message, ErrorType.NOT_A_MEMBER);
    }

    public static NotAMemberException fromId(Long userId, Long courseId) {
        return new NotAMemberException("User " + userId + " doesn't belong to the course: " + courseId);
    }
}
