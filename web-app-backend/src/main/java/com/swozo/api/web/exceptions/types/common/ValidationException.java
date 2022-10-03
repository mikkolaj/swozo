package com.swozo.api.web.exceptions.types.common;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;
import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends ApiException {
    public ValidationException(String message, Map<String, ValidationError> fieldErrors) {
        super(message, ErrorType.VALIDATION_FAILED, Map.copyOf(fieldErrors));
    }
}
