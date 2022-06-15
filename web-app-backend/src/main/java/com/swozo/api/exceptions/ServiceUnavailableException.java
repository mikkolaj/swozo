package com.swozo.api.exceptions;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(ServiceType serviceType) {
        super(serviceType.name() + " not available");
    }
}
