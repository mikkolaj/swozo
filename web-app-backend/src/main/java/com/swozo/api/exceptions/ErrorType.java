package com.swozo.api.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    COURSE_NOT_FOUND(ApiError.RESOURCE_DOESNT_EXIST),
    USER_NOT_FOUND(ApiError.RESOURCE_DOESNT_EXIST),
    INVALID_COURSE_PASSWORD(ApiError.INVALID_CREDENTIALS),
    NOT_A_CREATOR(ApiError.UNAUTHORIZED),
    ALREADY_A_MEMBER(ApiError.ILLEGAL_STATE),
    VALIDATION_FAILED(ApiError.VALIDATION_FAILED);

    @JsonIgnore
    private final ApiError apiError;
}
