package com.swozo.webservice.exceptions;

public class ActivityNotFoundException extends RuntimeException {

    public ActivityNotFoundException(Long id) {
        super("Could not find activity " + id);
    }
}
