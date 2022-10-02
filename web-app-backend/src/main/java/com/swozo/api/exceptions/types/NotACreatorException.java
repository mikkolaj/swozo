package com.swozo.api.exceptions.types;

import com.swozo.api.exceptions.ApiException;
import com.swozo.api.exceptions.ErrorType;

public class NotACreatorException extends ApiException {
    public NotACreatorException(String message) {
        super(message, ErrorType.NOT_A_CREATOR);
    }

}
