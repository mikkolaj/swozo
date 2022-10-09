package com.swozo.api.web.exceptions.types.user;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

import java.util.Map;

public class UserNotFoundException extends ApiException {
    private UserNotFoundException(String message) {
        super(message, ErrorType.USER_NOT_FOUND);
    }

    private UserNotFoundException(String message, Map<String, Object> additionalData) {
        super(message, ErrorType.USER_NOT_FOUND, additionalData);
    }

    public static UserNotFoundException ofAuthenticationOwner() {
        return new UserNotFoundException("User with provided credentials no longer exists", Map.of("requireLogout", true));
    }

    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException("User " + email + " doesn't exist");
    }
}
