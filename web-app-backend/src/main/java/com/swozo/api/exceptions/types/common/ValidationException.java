package com.swozo.api.exceptions.types.common;

import com.swozo.api.exceptions.ApiException;
import com.swozo.api.exceptions.ErrorType;
import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends ApiException {
    public ValidationException(String message, Map<String, ValidationError> fieldErrors) {
        super(message, ErrorType.VALIDATION_FAILED, Map.copyOf(fieldErrors));
    }
}
