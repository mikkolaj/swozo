package com.swozo.webservice.exceptions;

public class ActivityModuleNotFoundException extends RuntimeException {

    public ActivityModuleNotFoundException(Long id) {
        super("Could not find activity module " + id);
    }
}
