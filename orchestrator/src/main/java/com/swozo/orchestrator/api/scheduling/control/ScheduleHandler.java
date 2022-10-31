package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleTypeMapper;
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

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

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
    private final ScheduleTypeMapper scheduleTypeMapper;
    private final TimingService timingService;
    private final BackendRequestSender requestSender;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduleEntityWithProvisioner startTracking(ScheduleRequest request) throws InvalidParametersException {
        var provisioner = getProvisioner(request);
        if (isTooLate(request, provisioner)) {
            throw new IllegalArgumentException("End time is before earliest possible ready time.");
        } else if (lastsNotLongEnough(request, provisioner)) {
            throw new IllegalArgumentException("End time is before estimated ready time.");
        } else {
            provisioner.validateParameters(request.dynamicProperties());
            var requestEntity = scheduleRequestTracker.startTracking(request);
            return new ScheduleEntityWithProvisioner(requestEntity, provisioner);
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
        delegateScheduling(requestEntity, getProvisioner(requestEntity));
    }

    public void delegateScheduling(ScheduleRequestEntity requestEntity, TimedSoftwareProvisioner provisioner) {
        var offset =
                timingService.getSchedulingOffset(requestEntity, provisioner.getProvisioningSeconds());
        logger.info("Scheduling request [id: {}] in {} seconds.", requestEntity.getId(), offset);
        scheduler.schedule(() -> scheduleCreationAndDeletion(requestEntity, provisioner), offset);
    }

    private Void scheduleCreationAndDeletion(
            ScheduleRequestEntity request,
            TimedSoftwareProvisioner provisioner
    ) throws InterruptedException {
        try {
            scheduleRequestTracker.updateStatus(request.getId(), VM_CREATING);
            var requestDto = requestMapper.toDto(request);
            timedVmProvider
                    .createInstance(requestDto.psm(), buildVmNamePrefix(request))
                    .thenAccept(provisionSoftwareAndScheduleDeletion(request, provisioner))
                    .get();
        } catch (ExecutionException e) {
            switch (e.getCause()) {
                case VMOperationFailed ex -> handleFailedVmCreation(request, ex);
                default -> handleUnexpectedException(request, e.getCause(), true);
            }
        } catch (Exception e) {
            handleUnexpectedException(request, e, false);
            throw e;
        }
        return null;
    }

    private void handleFailedVmCreation(ScheduleRequestEntity request, VMOperationFailed ex) {
        logger.error("Error while creating instance for request [id: {}]", request.getId(), ex);
        scheduleRequestTracker.markAsFailure(request.getId());
    }

    private void handleUnexpectedException(ScheduleRequestEntity request, Throwable ex, boolean log) {
        if (log)
            logger.error("Unexpected exception. Request: {}", request, ex);
        scheduleRequestTracker.markAsFailure(request.getId());
    }

    @Transactional
    public Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(
            ScheduleRequestEntity request
    ) {
        return provisionSoftwareAndScheduleDeletion(request, getProvisioner(request));
    }

    public Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(
            ScheduleRequestEntity request,
            TimedSoftwareProvisioner provisioner
    ) {
        return resourceDetails -> {
            try {
                switchToProvisioningState(request, resourceDetails);
                var links = delegateProvisioning(request, provisioner, resourceDetails);
                switchToReadyState(request, links);
                scheduleInstanceDeletion(request, resourceDetails.internalResourceId());
            } catch (ProvisioningFailed e) {
                logger.error("Provisioning request [id: {}] on: {} failed.", request.getId(), resourceDetails, e);
                scheduleRequestTracker.markAsFailure(request.getId());
            }
        };
    }

    @Transactional
    protected void switchToProvisioningState(ScheduleRequestEntity request, VMResourceDetails resourceDetails) {
        scheduleRequestTracker.updateStatus(request.getId(), PROVISIONING);
        scheduleRequestTracker.fillVmResourceId(request.getId(), resourceDetails.internalResourceId());
    }

    private List<ActivityLinkInfo> delegateProvisioning(ScheduleRequestEntity request, TimedSoftwareProvisioner provisioner, VMResourceDetails resourceDetails) {
        return CheckedExceptionConverter.from(
                () -> provisioner.provision(resourceDetails, request.getDynamicProperties()),
                ProvisioningFailed::new
        ).get();
    }

    @Transactional
    protected void switchToReadyState(ScheduleRequestEntity request, List<ActivityLinkInfo> links) {
        scheduleRequestTracker.updateStatus(request.getId(), READY);
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
                    .thenRun(() -> scheduleRequestTracker.updateStatus(request.getId(), DELETED));
        } catch (VMOperationFailed e) {
            logger.error("Deleting instance for request [id: {}] failed!", request, e);
        }
        return null;
    }

    public TimedSoftwareProvisioner getProvisioner(ScheduleRequestEntity requestEntity) {
        return provisionerFactory.getProvisioner(scheduleTypeMapper.toDto(requestEntity.getScheduleType()));
    }

    public TimedSoftwareProvisioner getProvisioner(ScheduleRequest request) {
        return provisionerFactory.getProvisioner(request.scheduleType());
    }


    public String buildVmNamePrefix(ScheduleRequestEntity request) {
        return String.format("%s-%s--%s--%s",
                request.getScheduleType().toString().toLowerCase(),
                request.getStartTime().format(formatter),
                request.getEndTime().format(formatter),
                request.getId()
        );
    }

    public record ScheduleEntityWithProvisioner(ScheduleRequestEntity entity, TimedSoftwareProvisioner provisioner) {
    }

    private static class FailedToSaveLinksException extends RuntimeException {
        public FailedToSaveLinksException(Throwable exception) {
            super(exception);
        }
    }
}
