package com.swozo.api.exceptions.types.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

@ToString
@EqualsAndHashCode
@Getter
public class ValidationError {
    private final String fieldName;
    private final ValidationErrorType errorType;
    private final Map<String, Object> args;

    public ValidationError(String fieldName, ValidationErrorType errorType) {
        this.fieldName = fieldName;
        this.errorType = errorType;
        this.args = new HashMap<>();
    }

    public ValidationError withArg(String argName, Object value) {
        args.put(argName, value);
        return this;
    }

}
