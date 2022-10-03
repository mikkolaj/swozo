package com.swozo.api.web.exceptions.types.course;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class AlreadyAMemberException extends ApiException {
    public AlreadyAMemberException(String message) {
        super(message, ErrorType.ALREADY_A_MEMBER);
    }
}
