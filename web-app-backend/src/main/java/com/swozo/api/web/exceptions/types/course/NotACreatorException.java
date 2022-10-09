package com.swozo.api.web.exceptions.types.course;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class NotACreatorException extends ApiException {
    public NotACreatorException(String message) {
        super(message, ErrorType.NOT_A_CREATOR);
    }
}
