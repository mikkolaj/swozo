package com.swozo.email;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class EmailFailedException extends ApiException {
    public EmailFailedException(String message) {
        super(message, ErrorType.FAILED_TO_SEND_EMAIL);
    }
}
