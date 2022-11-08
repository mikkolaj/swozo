package com.swozo.orchestrator.api.scheduling.persistence.entity;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ServiceStatus {

    SUBMITTED,
    VM_CREATING,
    VM_CREATION_FAILED,
    PROVISIONING,
    PROVISIONING_FAILED,
    READY,
    EXPORTING,
    EXPORT_FAILED,
    EXPORT_COMPLETE,
    DELETED;

    public ServiceStatus getNextErrorStatus() throws IllegalStateException {
        return switch (this) {
            case VM_CREATING, VM_CREATION_FAILED -> VM_CREATION_FAILED;
            case PROVISIONING, PROVISIONING_FAILED -> PROVISIONING_FAILED;
            case READY, EXPORTING, EXPORT_FAILED -> EXPORT_FAILED;
            default -> throw new IllegalStateException("Unrecognized failure reason.");
        };
    }

    public static Set<ServiceStatus> withoutVm() {
        return Set.of(
                SUBMITTED,
                VM_CREATING,
                VM_CREATION_FAILED
        );
    }

    public static Set<ServiceStatus> provisioning() {
        return Set.of(
                PROVISIONING,
                PROVISIONING_FAILED
        );
    }

    public static Set<ServiceStatus> notYetReady() {
        return Stream.concat(
                withoutVm().stream(),
                provisioning().stream()
        ).collect(Collectors.toSet());
    }

    public static Set<ServiceStatus> exporting() {
        return Set.of(
                EXPORTING,
                EXPORT_FAILED
        );
    }

    public static Set<ServiceStatus> toBeCleanedAndTerminated() {
        return Stream.concat(
                Stream.of(READY),
                exporting().stream()
        ).collect(Collectors.toSet());
    }

    public static ServiceStatus toBeDeleted() {
        return EXPORT_COMPLETE;
    }
}
