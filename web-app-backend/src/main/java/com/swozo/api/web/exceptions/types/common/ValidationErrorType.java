package com.swozo.api.web.exceptions.types.common;

public enum ValidationErrorType {
    MISSING,
    NOT_UNIQUE,
    START_TIME_AFTER_END,
    TOO_SHORT_DURATION,
    TOO_LONG_DURATION,
    TOO_SOON,
    TOO_SHORT_PERIOD_BETWEEN,
    NOT_IN_BOUNDS,
    INVALID_PASSWORD,
    INVALID_PASSWORD_TOKEN
    ;

    public ValidationError forField(String fieldName) {
        return new ValidationError(fieldName, this);
    }
}
