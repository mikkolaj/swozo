package com.swozo.api.web.exceptions;

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
    VALIDATION_FAILED(HttpStatus.CONFLICT),
    THIRD_PARTY_ERROR(HttpStatus.TOO_MANY_REQUESTS) // probably should be 503 but handling 4xx errors on client side is more convenient
    ;

    private final HttpStatus statusCode;
}
