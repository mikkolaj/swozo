package com.swozo.api.exceptions;

import lombok.Getter;

import java.util.Map;
import java.util.Optional;

@Getter
public abstract class ApiException extends RuntimeException {
    private final ErrorType errorType;
    private final Map<String, Object> additionalData;

    private record SerializableError(
            ErrorType errorType,
            String message,
            Optional<Map<String, Object>> additionalData
    ){}

    public ApiException(String message, ErrorType errorType) {
        this(message, errorType, null);
    }

    public ApiException(String message, ErrorType errorType, Map<String, Object> additionalData) {
        super(message);
        this.errorType = errorType;
        this.additionalData =  additionalData;
    }

    public SerializableError toSerializable() {
        return new SerializableError(errorType, getMessage(), Optional.ofNullable(additionalData));
    }

}
