package com.swozo.orchestrator.api.scheduling.persistence.entity;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import static com.swozo.orchestrator.utils.CollectionUtils.addElements;
import static com.swozo.orchestrator.utils.CollectionUtils.combineSets;

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

    public boolean canTransitionTo(ServiceStatus status) {
        return getValidFollowingStatuses().contains(status);
    }

    private Set<ServiceStatus> getValidFollowingStatuses() {
        var notReadyTransitions = Set.of(FAILED, CANCELLED);
        var withVmTransitions = Set.of(DELETED, CANCELLED);
        return switch (this) {
            case SUBMITTED, VM_CREATION_FAILED -> addElements(notReadyTransitions, VM_CREATING);
            case VM_CREATING -> addElements(notReadyTransitions, VM_CREATION_FAILED, PROVISIONING);
            case PROVISIONING ->
                    addElements(combineSets(notReadyTransitions, withVmTransitions), PROVISIONING_FAILED, READY, DELETED);
            case PROVISIONING_FAILED ->
                    addElements(combineSets(notReadyTransitions, withVmTransitions), PROVISIONING, DELETED);
            case READY -> addElements(withVmTransitions, WAITING_FOR_EXPORT);
            case WAITING_FOR_EXPORT -> addElements(withVmTransitions, EXPORT_FAILED, EXPORTING);
            case EXPORT_FAILED -> addElements(withVmTransitions, WAITING_FOR_EXPORT, EXPORTING);
            case EXPORTING -> addElements(withVmTransitions, EXPORT_FAILED, EXPORT_COMPLETE);
            case EXPORT_COMPLETE, FAILED, CANCELLED -> Set.of(DELETED);
            case DELETED -> Collections.emptySet();
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
        return combineSets(withoutVm(), provisioning());
    }

    public static Set<ServiceStatus> withVmBeforeExport() {
        return combineSets(provisioning(), toBeCheckedForExport());
    }

    public static Set<ServiceStatus> toBeCheckedForExport() {
        return addElements(wasInExportingState(), READY);
    }

    public static Set<ServiceStatus> wasInExportingState() {
        return Set.of(
                WAITING_FOR_EXPORT,
                EXPORTING,
                EXPORT_FAILED
        );
    }

    public static Set<ServiceStatus> readyToBeDeleted() {
        return addElements(canBeImmediatelyDeleted(), EXPORT_COMPLETE);
    }

    public static Set<ServiceStatus> toBeAborted() {
        return addElements(canBeImmediatelyDeleted(), DELETED);
    }

    public static Set<ServiceStatus> canBeImmediatelyDeleted() {
        return Set.of(FAILED, CANCELLED);
    }

    public static Collection<String> asStrings(Collection<ServiceStatus> statuses) {
        return statuses.stream().map(Objects::toString).toList();
    }
}
