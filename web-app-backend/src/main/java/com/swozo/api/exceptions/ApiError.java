package com.swozo.api.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ApiError {
    RESOURCE_DOESNT_EXIST(HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(HttpStatus.FORBIDDEN),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED),
    ILLEGAL_STATE(HttpStatus.BAD_REQUEST),
    VALIDATION_FAILED(HttpStatus.CONFLICT);

    @JsonIgnore
    private final HttpStatus statusCode;
}
