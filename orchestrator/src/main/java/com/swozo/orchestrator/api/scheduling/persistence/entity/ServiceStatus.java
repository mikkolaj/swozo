package com.swozo.orchestrator.api.scheduling.persistence.entity;

public enum ServiceStatus {

    SUBMITTED,
    VM_CREATING,
    VM_CREATION_FAILED,
    PROVISIONING,
    PROVISIONING_FAILED,
    READY,
    FAILED_TO_SCHEDULE_CLEANUP,
    WAITING_FOR_CLEANUP,
    CLEANING_UP,
    CLEANUP_COMPLETE,
    DELETED;

    public ServiceStatus getNextErrorStatus() throws IllegalStateException {
        return switch (this) {
            case VM_CREATING -> VM_CREATION_FAILED;
            case PROVISIONING -> PROVISIONING_FAILED;
            case READY -> FAILED_TO_SCHEDULE_CLEANUP;
            default -> throw new IllegalStateException("Unrecognized failure reason.");
        };
    }
}
