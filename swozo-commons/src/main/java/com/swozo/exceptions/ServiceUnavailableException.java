package com.swozo.exceptions;

import com.swozo.utils.ServiceType;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(ServiceType serviceType) {
        super(serviceType.name() + " not available");
    }

    public ServiceUnavailableException(ServiceType serviceType, Throwable cause) {
        super(serviceType.name() + " not available", cause);
    }
}
