package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.*;
import com.swozo.orchestrator.scheduler.InternalTaskScheduler;
import com.swozo.utils.CheckedExceptionConverter;
import com.swozo.utils.VoidMapper;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus.*;

@Component
@RequiredArgsConstructor
public class ScheduleHandler {
    // TODO: probably will change if we decide to execute tasks (e.g. saving user's files) before deleting the instance
    private static final int CLEANUP_SECONDS = 180;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-HH-mm-ss");
    private final InternalTaskScheduler scheduler;
    private final TimedVMProvider timedVmProvider;
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final ScheduleRequestTracker scheduleRequestTracker;
    private final ScheduleRequestMapper requestMapper;
    private final ScheduleRequestValidator validator;
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
        scheduler.schedule(() -> scheduleCreationAndDeletion(requestEntity), offset);
    }


    private Void scheduleCreationAndDeletion(ScheduleRequestEntity requestEntity) throws InterruptedException {
        try {
            scheduleRequestTracker.updateStatus(requestEntity, VM_CREATING);
            timedVmProvider
                    .createInstance(requestMapper.toPsm(requestEntity), buildVmNamePrefix(requestEntity, requestEntity))
                    .thenCompose(updateVmResourceId(requestEntity))
                    .thenAccept(provisionSoftwareAndScheduleDeletion(requestEntity))
                    .get();
        } catch (ExecutionException e) {
            switch (e.getCause()) {
                case VMOperationFailed ex -> handleFailedVmCreation(requestEntity, ex);
                default -> handleUnexpectedException(requestEntity, e.getCause(), true);
            }
        } catch (Exception e) {
            handleUnexpectedException(requestEntity, e, false);
            throw e;
        }
        return null;
    }

    private void handleFailedVmCreation(ScheduleRequestEntity requestEntity, VMOperationFailed ex) {
        logger.error("Error while creating instance for request [id: {}]", requestEntity.getId(), ex);
        scheduleRequestTracker.updateStatus(requestEntity, VM_CREATION_FAILED);
    }

    private void handleUnexpectedException(ScheduleRequestEntity requestEntity, Throwable ex, boolean log) {
        if (log)
            logger.error("Unexpected exception. Request: {}", requestEntity, ex);
    }

    private Function<VMResourceDetails, CompletionStage<VMResourceDetails>> updateVmResourceId(ScheduleRequestEntity request) {
        return resourceDetails -> {
            scheduleRequestTracker.fillVmResourceId(request.getId(), resourceDetails.internalResourceId());
            scheduleRequestTracker.updateStatus(request, PROVISIONING);
            return CompletableFuture.completedFuture(resourceDetails);
        };
    }

    public Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(ScheduleRequestEntity request) {
        return resourceDetails -> {
            var futureLinks = getFutureLinks(request, resourceDetails);

            futureLinks.forEach(descriptionWithLinks -> {
                try {
                    var links = CheckedExceptionConverter.from(
                            () -> descriptionWithLinks.links.get(),
                            ProvisioningFailed::new
                    ).get();
                    switchToReadyState(request, descriptionWithLinks.description, links);
                    scheduleExportAndDeletion(request, resourceDetails);
                } catch (ProvisioningFailed ex) {
                    logger.error("Provisioning request [id: {}] on: {} failed.", request.getId(), resourceDetails, ex);
                    scheduleRequestTracker.markAsFailure(descriptionWithLinks.description);
                }
            });
        };
    }

    private Stream<DescriptionWithLinks> getFutureLinks(ScheduleRequestEntity request, VMResourceDetails resourceDetails) {
        return request.getServiceDescriptions().stream()
                .filter(ServiceDescriptionEntity::canBeProvisioned)
                .map(description -> {
                    var provisioner = getProvisioner(description.getServiceType());
                    return new DescriptionWithLinks(description, delegateProvisioning(description, provisioner, resourceDetails));
                });
    }

    private CompletableFuture<List<ActivityLinkInfo>> delegateProvisioning(ServiceDescriptionEntity serviceDescription, TimedSoftwareProvisioner provisioner, VMResourceDetails resourceDetails) {
        return CheckedExceptionConverter.from(
                () -> provisioner.provision(resourceDetails, serviceDescription.getDynamicProperties()),
                ProvisioningFailed::new
        ).get();
    }

    private void switchToReadyState(ScheduleRequestEntity request, ServiceDescriptionEntity description, List<ActivityLinkInfo> links) {
        scheduleRequestTracker.updateStatus(description, READY);
        try {
            CheckedExceptionConverter.from(
                    () -> requestSender.putActivityLinks(request.getId(), links).get(),
                    FailedToSaveLinksException::new
            ).get();
        } catch (FailedToSaveLinksException ex) {
            logger.error("Unable to send links to backend.", ex);
        }
    }

    public void scheduleExportAndDeletion(ScheduleRequestEntity requestEntity, VMResourceDetails resourceDetails) {
        requestEntity.getServiceDescriptions().forEach(description ->
                provisionerFactory.getProvisioner(description.getServiceType())
                        .getWorkdirToSave()
                        .ifPresent(workdir ->
                                scheduleWorkspaceExport(workdir, requestEntity, description, resourceDetails)
                        )
        );
        scheduleDeletion(requestEntity, resourceDetails, CLEANUP_SECONDS);
    }

    private void scheduleWorkspaceExport(String workdirPath, ScheduleRequestEntity requestEntity, ServiceDescriptionEntity description, VMResourceDetails resourceDetails) {
        var exportOffset = timingService.getExportOffset(requestEntity);
        logger.info("Scheduling instance cleanup for request [id: {}] and serviceType: {} in {} seconds", requestEntity.getId(), description.getServiceType(), exportOffset);
        scheduler.schedule(VoidMapper.toCallableVoid(() -> exporter.exportToBucket(
                resourceDetails,
                workdirPath,
                requestEntity,
                description
        )), exportOffset);
    }

    public void scheduleDeletion(ScheduleRequestEntity requestEntity, VMResourceDetails resourceDetails) {
        scheduleDeletion(requestEntity, resourceDetails, CLEANUP_SECONDS);
    }

    private void scheduleDeletion(ScheduleRequestEntity requestEntity, VMResourceDetails resourceDetails, int cleanupSeconds) {
        var deletionOffset = timingService.getDeletionOffset(requestEntity, cleanupSeconds);
        logger.info("Scheduling instance deletion for request [id: {}] in {} seconds", requestEntity.getId(), deletionOffset);
        scheduler.schedule(() -> deleteIfAllExported(requestEntity, resourceDetails.internalResourceId()), deletionOffset);
    }

    public Void deleteIfAllExported(ScheduleRequestEntity request, long internalResourceId) throws InterruptedException {
        try {
            var failedExportsCount = processServicesWithFailedExport(request, internalResourceId);

            if (failedExportsCount > 0) {
                logger.warn(
                        "Failed to export all user data before deadline for request [id: {}]. You have {} seconds to collect it manually.",
                        request.getId(), TimingService.MANUAL_CLEANUP_SECONDS
                );
                scheduler.schedule(callDelete(request, internalResourceId), TimingService.MANUAL_CLEANUP_SECONDS);
            } else {
                delete(request, internalResourceId);
            }
        } catch (VMOperationFailed e) {
            logger.error("Deleting instance for request [id: {}] failed!", request, e);
        }
        return null;
    }

    private Callable<Void> callDelete(ScheduleRequestEntity request, long internalResourceId) {
        return VoidMapper.toCallableVoid(() -> delete(request, internalResourceId));
    }

    private void delete(ScheduleRequestEntity request, long internalResourceId) throws InterruptedException {
        timedVmProvider.deleteInstance(internalResourceId)
                .thenRun(() -> request.getServiceDescriptions().forEach(description ->
                        scheduleRequestTracker.updateStatus(description, DELETED))
                );
    }

    private long processServicesWithFailedExport(ScheduleRequestEntity request, long internalResourceId) {
        return request.getServiceDescriptions()
                .stream()
                .filter(ServiceDescriptionEntity::isNotReadyToBeDeleted)
                .map(failedService -> {
                    logger.warn("{} couldn't be cleaned up on instance with id: {}. Save progress manually within {} seconds", request, internalResourceId, TimingService.MANUAL_CLEANUP_SECONDS);
                    return failedService;
                }).count();
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


    private record DescriptionWithLinks(
            ServiceDescriptionEntity description,
            CompletableFuture<List<ActivityLinkInfo>> links
    ) {
    }

    private static class FailedToSaveLinksException extends RuntimeException {
        public FailedToSaveLinksException(Throwable exception) {
            super(exception);
        }
    }
}
