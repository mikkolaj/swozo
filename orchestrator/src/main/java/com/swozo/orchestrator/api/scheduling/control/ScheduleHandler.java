package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.users.links.ActivityLinkInfo;
import com.swozo.orchestrator.api.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ServiceTypeMapper;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.InvalidParametersException;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisionerFactory;
import com.swozo.orchestrator.scheduler.InternalTaskScheduler;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus.*;

@Component
@RequiredArgsConstructor
public class ScheduleHandler {
    // TODO: probably will change if we decide to execute tasks (e.g. saving user's files) before deleting the instance
    private static final int CLEANUP_SECONDS = 0;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-HH-mm-ss");
    private final InternalTaskScheduler scheduler;
    private final TimedVMProvider timedVmProvider;
    private final TimedSoftwareProvisionerFactory provisionerFactory;
    private final ScheduleRequestTracker scheduleRequestTracker;
    private final ScheduleRequestMapper requestMapper;
    private final ServiceTypeMapper serviceTypeMapper;
    private final TimingService timingService;
    private final BackendRequestSender requestSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduleRequestEntity startTracking(ScheduleRequest request) throws InvalidParametersException {
        request.serviceDescriptions().forEach(description -> {
            var provisioner = getProvisioner(serviceTypeMapper.toPersistence(description.serviceType()));
            checkTimeBounds(request, provisioner);
            provisioner.validateParameters(description.dynamicProperties());
        });
        return scheduleRequestTracker.startTracking(request);
    }

    private void checkTimeBounds(ScheduleRequest request, TimedSoftwareProvisioner provisioner) {
        if (isTooLate(request, provisioner)) {
            throw new IllegalArgumentException("End time is before earliest possible ready time.");
        } else if (lastsNotLongEnough(request, provisioner)) {
            throw new IllegalArgumentException("End time is before estimated ready time.");
        }
    }

    private boolean isTooLate(ScheduleRequest request, TimedSoftwareProvisioner provisioner) {
        var earliestReadyTime = LocalDateTime.now().plusSeconds(provisioner.getProvisioningSeconds());
        return request.serviceLifespan().endTime()
                .isBefore(earliestReadyTime);
    }

    private boolean lastsNotLongEnough(ScheduleRequest request, TimedSoftwareProvisioner provisioner) {
        var estimatedReadyTime =
                request.serviceLifespan().startTime().plusSeconds(provisioner.getProvisioningSeconds());
        return request.serviceLifespan().endTime()
                .isBefore(estimatedReadyTime);
    }

    public void delegateScheduling(ScheduleRequestEntity requestEntity) {
        requestEntity.getServiceDescriptions().forEach(serviceDescription -> {
            var provisioner =
                    provisionerFactory.getProvisioner(serviceDescription.getServiceType());
            var offset = timingService.getSchedulingOffset(requestEntity, provisioner.getProvisioningSeconds());
            logger.info("Scheduling service {} for request [id: {}] in {} seconds.", serviceDescription, requestEntity.getId(), offset);
            scheduler.schedule(() -> scheduleCreationAndDeletion(requestEntity, serviceDescription, provisioner), offset);
        });
    }

    private Void scheduleCreationAndDeletion(
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity description,
            TimedSoftwareProvisioner provisioner
    ) throws InterruptedException {
        try {
            scheduleRequestTracker.updateStatus(description, VM_CREATING);
            timedVmProvider
                    .createInstance(requestMapper.toPsm(requestEntity), buildVmNamePrefix(requestEntity, description))
                    .thenCompose(updateVmResourceId(requestEntity))
                    .thenAccept(provisionSoftwareAndScheduleDeletion(requestEntity, description, provisioner))
                    .get();
        } catch (ExecutionException e) {
            switch (e.getCause()) {
                case VMOperationFailed ex -> handleFailedVmCreation(description, ex);
                default -> handleUnexpectedException(description, e.getCause(), true);
            }
        } catch (Exception e) {
            handleUnexpectedException(description, e, false);
            throw e;
        }
        return null;
    }

    private void handleFailedVmCreation(ServiceDescriptionEntity description, VMOperationFailed ex) {
        logger.error("Error while creating instance for request [id: {}]", description.getId(), ex);
        scheduleRequestTracker.markAsFailure(description);
    }

    private void handleUnexpectedException(ServiceDescriptionEntity description, Throwable ex, boolean log) {
        if (log)
            logger.error("Unexpected exception. Request: {}", description, ex);
        scheduleRequestTracker.markAsFailure(description);
    }

    private Function<VMResourceDetails, CompletionStage<VMResourceDetails>> updateVmResourceId(ScheduleRequestEntity request) {
        return resourceDetails -> {
            scheduleRequestTracker.fillVmResourceId(request.getId(), resourceDetails.internalResourceId());
            return CompletableFuture.completedFuture(resourceDetails);
        };
    }

    public Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(
            ScheduleRequestEntity request,
            ServiceDescriptionEntity description
    ) {
        return provisionSoftwareAndScheduleDeletion(request, description, getProvisioner(description.getServiceType()));
    }

    public Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(
            ScheduleRequestEntity request,
            ServiceDescriptionEntity description,
            TimedSoftwareProvisioner provisioner
    ) {
        return resourceDetails -> {
            try {
                switchToProvisioningState(request, description, resourceDetails);
                var links = delegateProvisioning(description, provisioner, resourceDetails);
                switchToReadyState(request, description, links);
                scheduleInstanceDeletion(request, resourceDetails.internalResourceId());
            } catch (ProvisioningFailed e) {
                logger.error("Provisioning request [id: {}] on: {} failed.", request.getId(), resourceDetails, e);
                scheduleRequestTracker.markAsFailure(description);
            }
        };
    }

    private void switchToProvisioningState(ScheduleRequestEntity request, ServiceDescriptionEntity description, VMResourceDetails resourceDetails) {
        scheduleRequestTracker.updateStatus(description, PROVISIONING);
        scheduleRequestTracker.fillVmResourceId(request.getId(), resourceDetails.internalResourceId());
    }

    private List<ActivityLinkInfo> delegateProvisioning(ServiceDescriptionEntity serviceDescription, TimedSoftwareProvisioner provisioner, VMResourceDetails resourceDetails) {
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

    public void scheduleInstanceDeletion(ScheduleRequestEntity requestEntity, long internalResourceId) {
        var deletionOffset = timingService.getDeletionOffset(requestEntity, CLEANUP_SECONDS);
        logger.info("Scheduling instance deletion for request [id: {}] in {} seconds", requestEntity.getId(), deletionOffset);
        scheduler.schedule(() -> deleteInstance(requestEntity, internalResourceId), deletionOffset);
    }

    public Void deleteInstance(ScheduleRequestEntity request, long internalResourceId) throws InterruptedException {
        try {
            timedVmProvider.deleteInstance(internalResourceId)
                    .thenRun(() -> request.getServiceDescriptions().forEach(description ->
                            scheduleRequestTracker.updateStatus(description, DELETED))
                    );
        } catch (VMOperationFailed e) {
            logger.error("Deleting instance for request [id: {}] failed!", request, e);
        }
        return null;
    }

    public TimedSoftwareProvisioner getProvisioner(ServiceTypeEntity serviceType) {
        return provisionerFactory.getProvisioner(serviceType);
    }


    public String buildVmNamePrefix(ScheduleRequestEntity request, ServiceDescriptionEntity description) {
        return String.format("%s-%s--%s--%s",
                description.getServiceType().toString().toLowerCase(),
                request.getStartTime().format(formatter),
                request.getEndTime().format(formatter),
                request.getId()
        );
    }

    private static class FailedToSaveLinksException extends RuntimeException {
        public FailedToSaveLinksException(Throwable exception) {
            super(exception);
        }
    }
}
