package com.swozo.api.exceptions.types;

import com.swozo.api.exceptions.ApiException;
import com.swozo.api.exceptions.ErrorType;

public class AlreadyAMemberException extends ApiException {
    public AlreadyAMemberException(String message) {
        super(message, ErrorType.ALREADY_A_MEMBER);
    }
}
