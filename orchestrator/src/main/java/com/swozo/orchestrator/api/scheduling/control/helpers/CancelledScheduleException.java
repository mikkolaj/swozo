package com.swozo.orchestrator.api.scheduling.control.helpers;

public class CancelledScheduleException extends RuntimeException {
    public CancelledScheduleException(String message) {
        super(message);
    }
}
