package com.swozo.orchestrator.api.scheduling.persistence.entity;

public enum RequestStatus {

    SUBMITTED,
    VM_CREATING,
    VM_CREATION_FAILED,
    PROVISIONING,
    PROVISIONING_FAILED,
    READY,
    DELETED;

    public RequestStatus getNextErrorStatus() throws IllegalStateException {
        return switch (this) {
            case VM_CREATING -> VM_CREATION_FAILED;
            case PROVISIONING -> PROVISIONING_FAILED;
            default -> throw new IllegalStateException("Unrecognized failure reason.");
        };
    }
}
