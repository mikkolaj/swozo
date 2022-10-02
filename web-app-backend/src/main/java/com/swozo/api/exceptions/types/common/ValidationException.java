package com.swozo.api.exceptions.types.common;

import com.swozo.api.exceptions.ApiException;
import com.swozo.api.exceptions.ErrorType;
import lombok.Getter;

import java.util.Map;

@Getter
public class ValidationException extends ApiException {
    private final Map<String, String> fieldErrors;

    public ValidationException(String message, ErrorType errorType, Map<String, String> fieldErrors) {
        super(message, errorType);
        this.fieldErrors = fieldErrors;
    }
}
