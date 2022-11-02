package com.swozo.api.web.exceptions.types.user;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class InvalidCredentialsException extends ApiException {
    public InvalidCredentialsException(String message) {
        super(message, ErrorType.INVALID_CREDENTIALS);
    }
}
