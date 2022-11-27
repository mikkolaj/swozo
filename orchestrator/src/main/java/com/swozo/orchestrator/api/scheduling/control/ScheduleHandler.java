package com.swozo.orchestrator.api.scheduling.control;

import com.google.common.collect.Sets;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.control.helpers.*;
import com.swozo.orchestrator.api.scheduling.persistence.TransactionalRequestUtils;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.cloud.resources.vm.ResourceNoLongerExists;
import com.swozo.orchestrator.cloud.resources.vm.TimedVmProvider;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import com.swozo.orchestrator.cloud.software.WorkspaceExporter;
import com.swozo.orchestrator.scheduler.InternalTaskScheduler;
import com.swozo.utils.RetryHandler;
import com.swozo.utils.VoidMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus.*;
import static com.swozo.utils.LoggingUtils.log;
import static com.swozo.utils.LoggingUtils.logIfError;

@Component
@RequiredArgsConstructor
public class ScheduleHandler {
    // TODO: probably will change if we decide to execute tasks (e.g. saving user's files) before deleting the instance
    private static final int EXPORT_SECONDS = 180;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-HH-mm-ss");
    private final InternalTaskScheduler scheduler;
    private final TimedVmProvider timedVmProvider;
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final ScheduleRequestTracker scheduleRequestTracker;
    private final TransactionalRequestUtils requestUtils;
    private final ScheduleRequestValidator validator;
    private final ScheduleRequestFilter requestFilter;
    private final TimingService timingService;
    private final BackendRequestSender requestSender;
    private final WorkspaceExporter exporter;
    private final AbortHandler abortHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduleRequestEntity startTracking(ScheduleRequest request) throws InvalidParametersException, IllegalArgumentException {
        validator.validate(request);
        return scheduleRequestTracker.startTracking(request);
    }

    public void cancel(long scheduleRequestId) {
        scheduler.cancelAllTasks(scheduleRequestId);
        scheduleRequestTracker.stopTracking(scheduleRequestId);
        scheduleConditionalDeletion(scheduleRequestTracker.getScheduleRequest(scheduleRequestId), false);
    }

    public void continueSchedulingFlowAfterFailure(ScheduleRequestEntity requestEntity) {
        requestEntity.getVmResourceId().ifPresentOrElse(
                id -> continueProvisioningWithPresentVm(requestEntity, id),
                () -> delegateScheduling(requestEntity)
        );
    }

    private CompletableFuture<Void> continueProvisioningWithPresentVm(ScheduleRequestEntity requestEntity, long vmId) {
        var inProvisioning = requestEntity.getServicesWithStatusesIn(ServiceStatus.provisioning());
        var toBeCheckedForExport = requestEntity.getServicesWithStatusesIn(ServiceStatus.toBeCheckedForExport());
        return continueProvisioningWithPresentVm(requestEntity, inProvisioning, toBeCheckedForExport, vmId);
    }

    public CompletableFuture<Void> continueProvisioningWithPresentVm(
            ScheduleRequestEntity requestEntity,
            List<ServiceDescriptionEntity> servicesToReprovision,
            List<ServiceDescriptionEntity> servicesToExport,
            long vmId
    ) {
        return timedVmProvider.getVMResourceDetails(vmId)
                .whenComplete(handleMissingResourceDetails(requestEntity))
                .thenApply(resourceDetails -> {
                    var validServices =
                            cancelInvalidServicesAndGetValid(requestEntity, new HashSet<>(servicesToReprovision));
                    CompletableFuture.runAsync(() -> startFromProvisioning(requestEntity, validServices).apply(resourceDetails))
                            .whenComplete(logIfError(logger, "Failure during provisioning retry"));
                    CompletableFuture.runAsync(() -> startFromExport(requestEntity, servicesToExport).apply(resourceDetails))
                            .whenComplete(logIfError(logger, "Failure during export retry"));
                    return resourceDetails;
                })
                .thenAccept(scheduleConditionalDeletion(requestEntity, EXPORT_SECONDS));
    }

    private BiConsumer<VmResourceDetails, Throwable> handleMissingResourceDetails(ScheduleRequestEntity requestEntity) {
        return (msg, ex) -> {
            if (ex != null && ex.getCause() instanceof ResourceNoLongerExists) {
                logger.warn("Vm has already been deleted [id: {}]", requestEntity.getId(), ex.getCause());
                scheduleRequestTracker.updateStatus(requestEntity, DELETED);
            }
        };
    }

    public void delegateScheduling(ScheduleRequestEntity requestEntity) {
        var longestProvisioningTime = getTotalProvisioningTime(requestEntity);
        var offset = timingService.getSchedulingOffset(requestEntity, longestProvisioningTime);
        logger.info("Creating VM for request [id: {}] in {} seconds.", requestEntity.getId(), offset);
        scheduler.scheduleCancellableTask(
                requestEntity.getId(),
                () -> scheduleCreationAndDeletion(requestEntity),
                offset
        );
    }

    private Void scheduleCreationAndDeletion(ScheduleRequestEntity requestEntity) {
        scheduleRequestTracker.updateStatus(requestEntity, VM_CREATING);
        var resourceDetails = timedVmProvider
                .createInstance(requestUtils.toMdaVmSpecs(requestEntity), buildVmNamePrefix(requestEntity));
        resourceDetails
                .whenComplete(handlePossibleVmCreationFailure(requestEntity))
                .thenApply(updateVmResourceId(requestEntity))
                .thenCompose(startFromProvisioning(requestEntity, requestEntity.getServiceDescriptions()))
                .whenComplete(logIfError(logger, "Failure during provisioning.", CancelledScheduleException.class));
        resourceDetails
                .thenAccept(scheduleConditionalDeletion(requestEntity, EXPORT_SECONDS));
        return null;
    }

    private BiConsumer<VmResourceDetails, Throwable> handlePossibleVmCreationFailure(ScheduleRequestEntity requestEntity) {
        return (msg, ex) -> {
            if (ex != null) {
                logger.error("Error while creating instance for request [id: {}]", requestEntity.getId(), ex);
                scheduleRequestTracker.updateStatus(requestEntity, VM_CREATION_FAILED);
            }
        };
    }

    private Function<VmResourceDetails, VmResourceDetails> updateVmResourceId(ScheduleRequestEntity request) {
        return resourceDetails -> {
            scheduleRequestTracker.fillVmResourceId(request.getId(), resourceDetails.internalResourceId());
            abortHandler.abortRequestIfNecessary(request.getId());
            return resourceDetails;
        };
    }

    private Function<VmResourceDetails, CompletableFuture<VmResourceDetails>> startFromProvisioning(ScheduleRequestEntity requestEntity, List<ServiceDescriptionEntity> services) {
        return resourceDetails -> provisionSoftware(requestEntity, resourceDetails, services)
                .thenApply(abortIfNecessary(requestEntity))
                .thenApply(successfulServices ->
                        startFromExport(requestEntity, successfulServices).apply(resourceDetails)
                );
    }

    private UnaryOperator<List<ServiceDescriptionEntity>> abortIfNecessary(ScheduleRequestEntity requestEntity) {
        return result -> {
            abortHandler.abortRequestIfNecessary(requestEntity.getId());
            return result;
        };
    }

    private CompletableFuture<List<ServiceDescriptionEntity>> provisionSoftware(
            ScheduleRequestEntity request,
            VmResourceDetails resourceDetails,
            List<ServiceDescriptionEntity> services
    ) {
        return CompletableFuture.supplyAsync(() -> provisionSequentially(request, services, resourceDetails)
                .filter(DescriptionWithStatus::succeeded)
                .map(DescriptionWithStatus::description)
                .toList()
        );
    }

    private Stream<DescriptionWithStatus> provisionSequentially(
            ScheduleRequestEntity request,
            List<ServiceDescriptionEntity> services,
            VmResourceDetails resourceDetails
    ) {
        return services.stream()
                .map(description -> {
                    scheduleRequestTracker.updateStatus(description, PROVISIONING);
                    var provisioner = getProvisioner(description.getServiceType());
                    var futureStatus = provisionSingleService(request, resourceDetails, description, provisioner);
                    return new DescriptionWithStatus(description, waitForStatus(futureStatus));
                });
    }

    private CompletableFuture<Boolean> provisionSingleService(
            ScheduleRequestEntity request,
            VmResourceDetails resourceDetails,
            ServiceDescriptionEntity description,
            TimedSoftwareProvisioner provisioner
    ) {
        return provisioner.provision(request, description, resourceDetails)
                .thenCompose(switchToReadyState(request, description))
                .thenApply(successfulStateUpdate -> true)
                .exceptionally(handlePossibleProvisioningFailure(resourceDetails, description));
    }

    private Function<Throwable, Boolean> handlePossibleProvisioningFailure(
            VmResourceDetails resourceDetails, ServiceDescriptionEntity serviceDescription
    ) {
        return ex -> {
            if (!scheduleRequestTracker.serviceWasCancelled(serviceDescription.getId())) {
                logger.error("Provisioning service [id: {}] on: {} failed.", serviceDescription.getId(), resourceDetails, ex);
                scheduleRequestTracker.updateStatus(serviceDescription, PROVISIONING_FAILED);
            }
            return false;
        };
    }

    private boolean waitForStatus(CompletableFuture<Boolean> futureStatus) {
        try {
            return futureStatus.join();
        } catch (CompletionException | CancellationException ex) {
            logger.error("Failed to wait for links", ex);
            return false;
        }
    }

    private Function<VmResourceDetails, VmResourceDetails> startFromExport(
            ScheduleRequestEntity requestEntity, List<ServiceDescriptionEntity> services
    ) {
        return resourceDetails -> {
            services.forEach(description -> provisionerFactory.getProvisioner(description.getServiceType())
                    .getWorkdirToSave()
                    .ifPresent(workdir ->
                            scheduleWorkspaceExport(workdir, requestEntity, description, resourceDetails)
                    )
            );
            return resourceDetails;
        };
    }

    private void scheduleWorkspaceExport(String workdirPath, ScheduleRequestEntity requestEntity, ServiceDescriptionEntity description, VmResourceDetails resourceDetails) {
        var exportOffset = timingService.getExportOffset(requestEntity);
        scheduleRequestTracker.updateStatus(description, WAITING_FOR_EXPORT);
        logger.info("Scheduling export for request [id: {}] and serviceType: {} in {} seconds", requestEntity.getId(), description.getServiceType(), exportOffset);
        scheduler.scheduleCancellableTask(requestEntity.getId(), VoidMapper.toCallableVoid(() -> exporter.exportToBucket(
                resourceDetails,
                workdirPath,
                requestEntity,
                description
        )), exportOffset);
    }

    private List<ServiceDescriptionEntity> cancelInvalidServicesAndGetValid(ScheduleRequestEntity requestEntity, HashSet<ServiceDescriptionEntity> all) {
        var invalid = all.stream()
                .map(description -> description.toScheduleRequestWithServiceDescriptions(requestEntity))
                .filter(requestFilter::endsBeforeAvailability)
                .map(ScheduleRequestWithServiceDescription::description)
                .collect(Collectors.toSet());

        invalid.forEach(description -> {
            logger.info("Marking request with [id: {}] as FAILED.", requestEntity.getId());
            scheduleRequestTracker.updateStatus(description, FAILED);
        });
        return Sets.difference(all, invalid).stream().toList();
    }

    private Function<List<ActivityLinkInfo>, CompletableFuture<Void>> switchToReadyState(ScheduleRequestEntity
            request, ServiceDescriptionEntity description) {
        return links -> RetryHandler.withExponentialBackoff(
                        () -> requestSender.putActivityLinks(description.getActivityModuleId(), request.getId(), links), 5
                ).whenComplete(log(logger, "Successfully sent links to backend", "Unable to send links to backend."))
                .thenRun(() -> scheduleRequestTracker.updateStatus(description, READY));
    }

    public void scheduleConditionalDeletion(ScheduleRequestEntity requestEntity, boolean markAsDeletedIfNoVm) {
        requestEntity.getVmResourceId().ifPresentOrElse(
                vmResourceId -> scheduleConditionalDeletion(
                        requestEntity.getId(),
                        vmResourceId,
                        timingService.getDeletionOffset(requestEntity, EXPORT_SECONDS)
                ), () -> {
                    if (markAsDeletedIfNoVm)
                        scheduleRequestTracker.updateStatus(requestEntity, DELETED);
                }
        );
    }

    private Consumer<VmResourceDetails> scheduleConditionalDeletion(ScheduleRequestEntity requestEntity, int cleanupSeconds) {
        return resourceDetails -> scheduleConditionalDeletion(
                requestEntity.getId(),
                resourceDetails.internalResourceId(),
                timingService.getDeletionOffset(requestEntity, cleanupSeconds)
        );
    }

    private void scheduleConditionalDeletion(long scheduleRequestId, long internalVmId, long deletionOffset) {
        logger.info("Scheduling instance deletion for request [id: {}] in {} seconds", scheduleRequestId, deletionOffset);
        scheduler.scheduleCancellableTask(scheduleRequestId, () -> deleteIfAllExported(scheduleRequestId, internalVmId), deletionOffset);
    }

    private Void deleteIfAllExported(long scheduleRequestId, long internalResourceId) {
        var request = scheduleRequestTracker.getScheduleRequest(scheduleRequestId);
        var failedExportsCount = processServicesWithFailedExport(request, internalResourceId);

        if (failedExportsCount <= 0) {
            delete(request, internalResourceId);
        } else {
            logger.warn(
                    "Failed to export all user data before deadline for request [id: {}]. You have {} seconds to collect it manually.",
                    request.getId(), TimingService.MANUAL_CLEANUP_SECONDS
            );
            scheduler.schedule(() -> delete(request, internalResourceId), TimingService.MANUAL_CLEANUP_SECONDS);
        }
        return null;
    }

    private long processServicesWithFailedExport(ScheduleRequestEntity request, long internalResourceId) {
        return request.getServiceDescriptions()
                .stream()
                .filter(scheduleRequestTracker::shouldBeExported)
                .map(failedService -> {
                    logger.warn("Failed to export {} from instance with id: {}", failedService.getServiceType(), internalResourceId);
                    return failedService;
                }).count();
    }

    private Void delete(ScheduleRequestEntity request, long internalResourceId) {
        timedVmProvider.deleteInstance(internalResourceId)
                .thenRun(() -> request.getServiceDescriptions().forEach(this::updateStatusIfNecessary))
                .whenComplete(logIfError(logger, "Error during deleting instance"));
        return null;
    }

    private void updateStatusIfNecessary(ServiceDescriptionEntity description) {
        if (!scheduleRequestTracker.serviceWasDeleted(description.getId())) {
            scheduleRequestTracker.updateStatus(description, DELETED);
        }
    }

    private int getTotalProvisioningTime(ScheduleRequestEntity requestEntity) {
        return requestEntity.getServiceDescriptions().stream()
                .map(ServiceDescriptionEntity::getServiceType)
                .distinct()
                .map(provisionerFactory::getProvisioner)
                .map(TimedSoftwareProvisioner::getProvisioningSeconds)
                .reduce(Integer::sum)
                .orElseThrow(this::emptyServiceDescriptionsException);
    }

    public TimedSoftwareProvisioner getProvisioner(ServiceTypeEntity serviceType) {
        return provisionerFactory.getProvisioner(serviceType);
    }


    public String buildVmNamePrefix(ScheduleRequestEntity requestEntity) {
        var firstService = requestEntity.getServiceDescriptions()
                .stream()
                .findFirst()
                .map(ServiceDescriptionEntity::getServiceType)
                .orElseThrow(this::emptyServiceDescriptionsException)
                .toString()
                .toLowerCase();

        return String.format("%s-%s--%s--%s",
                firstService,
                requestEntity.getStartTime().format(formatter),
                requestEntity.getEndTime().format(formatter),
                requestEntity.getId()
        );
    }

    private IllegalArgumentException emptyServiceDescriptionsException() {
        return new IllegalArgumentException("Service Descriptions can't be empty.");
    }

    private record DescriptionWithStatus(
            ServiceDescriptionEntity description,
            boolean succeeded
    ) {
    }
}
