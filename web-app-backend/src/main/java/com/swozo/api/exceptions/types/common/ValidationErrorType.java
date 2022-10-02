package com.swozo.api.exceptions.types.common;

public enum ValidationErrorType {
    MISSING,
    NOT_UNIQUE,
    START_TIME_AFTER_END,
    TOO_SHORT_DURATION,
    TOO_LONG_DURATION,
    TOO_SOON
    ;

    public ValidationError forField(String fieldName) {
        return new ValidationError(fieldName, this);
    }
}
