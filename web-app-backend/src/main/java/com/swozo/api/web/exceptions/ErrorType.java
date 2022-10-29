package com.swozo.api.web.exceptions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorType {
    COURSE_NOT_FOUND(ApiError.RESOURCE_DOESNT_EXIST),
    ACTIVITY_NOT_FOUND(ApiError.RESOURCE_DOESNT_EXIST),
    USER_NOT_FOUND(ApiError.RESOURCE_DOESNT_EXIST),
    INVALID_COURSE_PASSWORD(ApiError.INVALID_CREDENTIALS),
    NOT_A_CREATOR(ApiError.UNAUTHORIZED),
    ALREADY_A_MEMBER(ApiError.ILLEGAL_STATE),
    NOT_A_MEMBER(ApiError.ILLEGAL_STATE),
    VALIDATION_FAILED(ApiError.VALIDATION_FAILED),
    DUPLICATE_FILE(ApiError.VALIDATION_FAILED),
    FILE_NOT_FOUND(ApiError.RESOURCE_DOESNT_EXIST),
    ILLEGAL_FILENAME(ApiError.VALIDATION_FAILED),
    SERVICE_MODULE_NOT_FOUND(ApiError.RESOURCE_DOESNT_EXIST)
    ;

    @JsonIgnore
    private final ApiError apiError;
}
