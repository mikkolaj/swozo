package com.swozo.api.web.exceptions.types.servicemodule;

import com.swozo.api.web.exceptions.ApiException;
import com.swozo.api.web.exceptions.ErrorType;

public class ServiceModuleNotFoundException extends ApiException {
    private ServiceModuleNotFoundException(String message) {
        super(message, ErrorType.SERVICE_MODULE_NOT_FOUND);
    }

    public static ServiceModuleNotFoundException ofReservation(Long reservationId) {
        return new ServiceModuleNotFoundException("Creation reservation for " + reservationId + " not found");
    }

    public static ServiceModuleNotFoundException of(Long serviceModuleId) {
        return new ServiceModuleNotFoundException("No service module with id: " + serviceModuleId);
    }

}
