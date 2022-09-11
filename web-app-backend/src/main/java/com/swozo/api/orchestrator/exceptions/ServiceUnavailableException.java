package com.swozo.api.orchestrator.exceptions;

import com.swozo.util.ServiceType;

public class ServiceUnavailableException extends RuntimeException {
    public ServiceUnavailableException(ServiceType serviceType) {
        super(serviceType.name() + " not available");
    }

    public ServiceUnavailableException(ServiceType serviceType, Throwable cause) {
        super(serviceType.name() + " not available", cause);
    }
}
