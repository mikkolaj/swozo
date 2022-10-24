package com.swozo.api.web.exceptions.types.course;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class ActivityNotFoundException extends ApiException {
    public ActivityNotFoundException(String message) {
        super(message, ErrorType.ACTIVITY_NOT_FOUND);
    }

    public static ActivityNotFoundException withId(Long id) {
        return new ActivityNotFoundException("Activity with id: " + id + " doesn't exist");
    }
}
