package com.swozo.orchestrator.api.scheduling.persistence.entity;

import java.util.Collection;
import java.util.Objects;
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
    WAITING_FOR_EXPORT,
    EXPORTING,
    EXPORT_FAILED,
    EXPORT_COMPLETE,
    DELETED,
    CANCELLED,
    FAILED;

    public ServiceStatus getNextErrorStatus() throws IllegalStateException {
        return switch (this) {
            case VM_CREATING, VM_CREATION_FAILED -> VM_CREATION_FAILED;
            case PROVISIONING, PROVISIONING_FAILED -> PROVISIONING_FAILED;
            case READY, WAITING_FOR_EXPORT, EXPORTING, EXPORT_FAILED -> EXPORT_FAILED;
            case CANCELLED -> CANCELLED;
            case DELETED -> DELETED;
            default -> FAILED;
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


    public static Set<ServiceStatus> withVmBeforeExport() {
        return Stream.concat(
                provisioning().stream(),
                toBeCheckedForExport().stream()
        ).collect(Collectors.toSet());
    }

    public static Set<ServiceStatus> toBeCheckedForExport() {
        return Stream.concat(
                Stream.of(READY),
                wasExporting().stream()
        ).collect(Collectors.toSet());
    }

    public static Set<ServiceStatus> wasExporting() {
        return Set.of(
                WAITING_FOR_EXPORT,
                EXPORTING,
                EXPORT_FAILED
        );
    }

    public static Set<ServiceStatus> readyToBeDeleted() {
        return Stream.concat(
                Stream.of(EXPORT_COMPLETE),
                canBeImmediatelyDeleted().stream()
        ).collect(Collectors.toSet());
    }

    public static Set<ServiceStatus> canBeImmediatelyDeleted() {
        return Set.of(CANCELLED);
    }

    public static Collection<String> asStrings(Collection<ServiceStatus> statuses) {
        return statuses.stream().map(Objects::toString).toList();
    }
}
