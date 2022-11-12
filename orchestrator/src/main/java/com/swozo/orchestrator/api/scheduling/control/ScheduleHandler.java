package com.swozo.orchestrator.api.scheduling.control;

import com.google.common.base.Functions;
import com.google.common.collect.Sets;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.control.helpers.ScheduleRequestFilter;
import com.swozo.orchestrator.api.scheduling.control.helpers.ScheduleRequestValidator;
import com.swozo.orchestrator.api.scheduling.control.helpers.ScheduleRequestWithServiceDescription;
import com.swozo.orchestrator.api.scheduling.persistence.TransactionalRequestUtils;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.cloud.resources.vm.TimedVmProvider;
import com.swozo.orchestrator.cloud.resources.vm.VmOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import com.swozo.orchestrator.cloud.software.WorkspaceExporter;
import com.swozo.orchestrator.scheduler.InternalTaskScheduler;
import com.swozo.utils.LoggingUtils;
import com.swozo.utils.VoidMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus.*;

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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduleRequestEntity startTracking(ScheduleRequest request) throws InvalidParametersException, IllegalArgumentException {
        validator.validate(request);
        return scheduleRequestTracker.startTracking(request);
    }

    public void delegateScheduling(ScheduleRequestEntity requestEntity) {
        var longestProvisioningTime = getLongestProvisioningTime(requestEntity);
        var offset = timingService.getSchedulingOffset(requestEntity, longestProvisioningTime);
        logger.info("Creating VM for request [id: {}] in {} seconds.", requestEntity.getId(), offset);
        scheduler.schedule(VoidMapper.toCallableVoid(() -> scheduleCreationAndDeletion(requestEntity)), offset);
    }

    public void applyAppropriateActionsOnServices(ScheduleRequestEntity requestEntity) {
        requestEntity.getVmResourceId().ifPresent(id -> {
                    var groupedByStatus = requestEntity.getServiceDescriptions()
                            .stream()
                            .collect(Collectors.groupingBy(ServiceDescriptionEntity::getStatus));

                    var resourceDetails = timedVmProvider.getVMResourceDetails(id);

                    groupedByStatus.forEach((status, services) -> {
                        switch (status) {
                            case PROVISIONING, PROVISIONING_FAILED -> {
                                var validServices = handleInvalidServices(requestEntity, new HashSet<>(services));
                                resourceDetails.thenCompose(startFromProvisioning(requestEntity, validServices));
                            }
                            case READY, WAITING_FOR_EXPORT, EXPORTING, EXPORT_FAILED ->
                                    resourceDetails.thenCompose(startFromExport(requestEntity, services));
                            default -> {
                            }
                        }
                    });

                    resourceDetails.thenAccept(scheduleConditionalDeletion(requestEntity, EXPORT_SECONDS));
                }
        );
    }

    private void scheduleCreationAndDeletion(ScheduleRequestEntity requestEntity) {
        scheduleRequestTracker.updateStatus(requestEntity, VM_CREATING);
        timedVmProvider
                .createInstance(requestUtils.toMdaVmSpecs(requestEntity), buildVmNamePrefix(requestEntity, requestEntity))
                .whenComplete(handleFailedVmCreation(requestEntity))
                .thenCompose(updateVmResourceId(requestEntity))
                .thenCompose(startFromProvisioning(requestEntity, requestEntity.getServiceDescriptions()))
                .thenAccept(scheduleConditionalDeletion(requestEntity, EXPORT_SECONDS));
    }


    private BiConsumer<VmResourceDetails, Throwable> handleFailedVmCreation(ScheduleRequestEntity requestEntity) {
        return (msg, ex) -> {
            if (ex != null) {
                logger.error("Error while creating instance for request [id: {}]", requestEntity.getId(), ex);
                scheduleRequestTracker.updateStatus(requestEntity, VM_CREATION_FAILED);
            }
        };
    }

    private Function<VmResourceDetails, CompletionStage<VmResourceDetails>> updateVmResourceId(ScheduleRequestEntity request) {
        return resourceDetails -> {
            scheduleRequestTracker.fillVmResourceId(request.getId(), resourceDetails.internalResourceId());
            scheduleRequestTracker.updateStatus(request, PROVISIONING);
            return CompletableFuture.completedFuture(resourceDetails);
        };
    }

    private Function<VmResourceDetails, CompletableFuture<VmResourceDetails>> startFromProvisioning(ScheduleRequestEntity requestEntity, List<ServiceDescriptionEntity> services) {
        return resourceDetails -> provisionSoftware(requestEntity, resourceDetails, services)
                .thenCompose(successfulServices ->
                        startFromExport(requestEntity, successfulServices).apply(resourceDetails)
                );
    }

    public CompletableFuture<List<ServiceDescriptionEntity>> provisionSoftware(
            ScheduleRequestEntity request,
            VmResourceDetails resourceDetails,
            List<ServiceDescriptionEntity> services
    ) {
        return CompletableFuture.supplyAsync(() -> getFutureLinks(services, resourceDetails)
                .map(waitForLinks(request, resourceDetails))
                .filter(DescriptionWithStatus::succeeded)
                .map(DescriptionWithStatus::description)
                .toList()
        );
    }

    private Stream<DescriptionWithFutureLinks> getFutureLinks(List<ServiceDescriptionEntity> services, VmResourceDetails resourceDetails) {
        return services.stream()
                .filter(ServiceDescriptionEntity::canBeProvisioned)
                .map(description -> {
                    var provisioner = getProvisioner(description.getServiceType());
                    return new DescriptionWithFutureLinks(
                            description,
                            provisioner.provision(resourceDetails, description.getDynamicProperties())
                    );
                });
    }

    private Function<DescriptionWithFutureLinks, DescriptionWithStatus> waitForLinks(ScheduleRequestEntity request, VmResourceDetails resourceDetails) {
        return descriptionWithFutureLinks -> {
            var serviceDescription = descriptionWithFutureLinks.description;

            var futureStatus = descriptionWithFutureLinks.links
                    .thenCompose(switchToReadyState(request, serviceDescription))
                    .thenCompose(Functions.constant(CompletableFuture.completedFuture(true)))
                    .exceptionally(handlePossibleProvisioningFailure(request, resourceDetails, serviceDescription));

            return new DescriptionWithStatus(serviceDescription, waitForStatus(futureStatus));
        };
    }

    private Function<Throwable, Boolean> handlePossibleProvisioningFailure(ScheduleRequestEntity request, VmResourceDetails resourceDetails, ServiceDescriptionEntity serviceDescription) {
        return ex -> {
            logger.error("Provisioning request [id: {}] on: {} failed.", request.getId(), resourceDetails, ex);
            scheduleRequestTracker.markAsFailure(serviceDescription);
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

    public Function<VmResourceDetails, CompletableFuture<VmResourceDetails>> startFromExport(ScheduleRequestEntity requestEntity, List<ServiceDescriptionEntity> services) {
        return resourceDetails -> {
            services.forEach(description -> provisionerFactory.getProvisioner(description.getServiceType())
                    .getWorkdirToSave()
                    .ifPresent(workdir ->
                            scheduleWorkspaceExport(workdir, requestEntity, description, resourceDetails)
                    )
            );
            return CompletableFuture.completedFuture(resourceDetails);
        };
    }

    private void scheduleWorkspaceExport(String workdirPath, ScheduleRequestEntity requestEntity, ServiceDescriptionEntity description, VmResourceDetails resourceDetails) {
        var exportOffset = timingService.getExportOffset(requestEntity);
        scheduleRequestTracker.updateStatus(description, WAITING_FOR_EXPORT);
        logger.info("Scheduling export for request [id: {}] and serviceType: {} in {} seconds", requestEntity.getId(), description.getServiceType(), exportOffset);
        scheduler.schedule(VoidMapper.toCallableVoid(() -> exporter.exportToBucket(
                resourceDetails,
                workdirPath,
                requestEntity,
                description
        )), exportOffset);
    }


    private List<ServiceDescriptionEntity> handleInvalidServices(ScheduleRequestEntity requestEntity, HashSet<ServiceDescriptionEntity> all) {
        var invalid = all.stream()
                .map(description -> description.toScheduleRequestWithServiceDescriptions(requestEntity))
                .filter(requestFilter::endsBeforeAvailability)
                .map(ScheduleRequestWithServiceDescription::description)
                .collect(Collectors.toSet());

        invalid.forEach(description -> scheduleRequestTracker.updateStatus(description, FAILED));
        return Sets.difference(all, invalid).stream().toList();
    }

    private Function<List<ActivityLinkInfo>, CompletableFuture<Void>> switchToReadyState(ScheduleRequestEntity request, ServiceDescriptionEntity description) {
        return links -> {
            scheduleRequestTracker.updateStatus(description, READY);
            return requestSender.putActivityLinks(request.getId(), links)
                    .exceptionally(LoggingUtils.logAndDefault(logger, "Unable to send links to backend.", null));
        };
    }

    public void scheduleConditionalDeletion(ScheduleRequestEntity requestEntity) {
        requestEntity.getVmResourceId().ifPresent(vmResourceId ->
                timedVmProvider.getVMResourceDetails(vmResourceId)
                        .thenAccept(scheduleConditionalDeletion(requestEntity, EXPORT_SECONDS))
        );
    }

    private Consumer<VmResourceDetails> scheduleConditionalDeletion(ScheduleRequestEntity requestEntity, int cleanupSeconds) {
        return resourceDetails -> {
            var deletionOffset = timingService.getDeletionOffset(requestEntity, cleanupSeconds);
            logger.info("Scheduling instance deletion for request [id: {}] in {} seconds", requestEntity.getId(), deletionOffset);
            scheduler.schedule(() -> deleteIfAllExported(requestEntity, resourceDetails.internalResourceId()), deletionOffset);
        };
    }

    private Void deleteIfAllExported(ScheduleRequestEntity request, long internalResourceId) {
        try {
            var failedExportsCount = processServicesWithFailedExport(request, internalResourceId);

            if (failedExportsCount <= 0) {
                delete(request, internalResourceId);
            } else {
                logger.warn(
                        "Failed to export all user data before deadline for request [id: {}]. You have {} seconds to collect it manually.",
                        request.getId(), TimingService.MANUAL_CLEANUP_SECONDS
                );
                scheduler.schedule(callDelete(request, internalResourceId), TimingService.MANUAL_CLEANUP_SECONDS);
            }
        } catch (VmOperationFailed e) {
            logger.error("Deleting instance for request [id: {}] failed!", request, e);
        }
        return null;
    }

    private long processServicesWithFailedExport(ScheduleRequestEntity request, long internalResourceId) {
        return request.getServiceDescriptions()
                .stream()
                .filter(ServiceDescriptionEntity::wasInExportingState)
                .map(failedService -> {
                    logger.warn("Failed to export from instance with id: {}", internalResourceId);
                    return failedService;
                }).count();
    }

    private Callable<Void> callDelete(ScheduleRequestEntity request, long internalResourceId) {
        return VoidMapper.toCallableVoid(() -> delete(request, internalResourceId));
    }

    private void delete(ScheduleRequestEntity request, long internalResourceId) {
        timedVmProvider.deleteInstance(internalResourceId)
                .thenRun(() -> request.getServiceDescriptions().forEach(description ->
                        scheduleRequestTracker.updateStatus(description, DELETED))
                );
    }

    private int getLongestProvisioningTime(ScheduleRequestEntity requestEntity) {
        return requestEntity.getServiceDescriptions().stream()
                .map(ServiceDescriptionEntity::getServiceType)
                .distinct()
                .map(provisionerFactory::getProvisioner)
                .map(TimedSoftwareProvisioner::getProvisioningSeconds)
                .max(Integer::compareTo)
                .orElseThrow(this::emptyServiceDescriptionsException);
    }

    public TimedSoftwareProvisioner getProvisioner(ServiceTypeEntity serviceType) {
        return provisionerFactory.getProvisioner(serviceType);
    }


    public String buildVmNamePrefix(ScheduleRequestEntity request, ScheduleRequestEntity requestEntity) {
        var firstService = requestEntity.getServiceDescriptions()
                .stream()
                .findFirst()
                .map(ServiceDescriptionEntity::getServiceType)
                .orElseThrow(this::emptyServiceDescriptionsException)
                .toString()
                .toLowerCase();

        return String.format("%s-%s--%s--%s",
                firstService,
                request.getStartTime().format(formatter),
                request.getEndTime().format(formatter),
                request.getId()
        );
    }

    private IllegalArgumentException emptyServiceDescriptionsException() {
        return new IllegalArgumentException("Service Descriptions can't be empty.");
    }

    private record DescriptionWithFutureLinks(
            ServiceDescriptionEntity description,
            CompletableFuture<List<ActivityLinkInfo>> links
    ) {
    }

    private record DescriptionWithStatus(
            ServiceDescriptionEntity description,
            boolean succeeded
    ) {
    }
}
