package com.swozo.orchestrator.api.scheduling.persistence.entity;

public enum RequestStatus {
    SUBMITTED,
    VM_CREATING,
    VM_CREATION_FAILED,
    PROVISIONING,
    PROVISIONING_FAILED,
    READY,
    DELETED;
}
