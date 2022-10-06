package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.function.ThrowingFunction;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.orchestrator.api.scheduling.persistence.entity.JupyterScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.mapper.ScheduleRequestMapper;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.ProvisioningFailed;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.scheduler.InternalTaskScheduler;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus.*;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    // TODO: probably will change if we decide to execute tasks (e.g. saving user's files) before deleting the instance
    private static final int CLEANUP_SECONDS = 0;
    private final InternalTaskScheduler scheduler;
    private final TimedVMProvider timedVmProvider;
    private final TimedSoftwareProvisioner jupyterProvisioner;
    private final ScheduleRequestTracker scheduleRequestTracker;
    private final ScheduleRequestMapper requestMapper;
    private final TimingService timingService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    // TODO: add retrying and cleaning of received, but not fulfilled requests
    public ScheduleResponse schedule(ScheduleRequest request) {
        var requestEntity = scheduleRequestTracker.startTracking(request);
        switch (requestEntity) {
            case JupyterScheduleRequestEntity jupyterRequest -> scheduler.schedule(
                    () -> scheduleCreationAndDeletion(jupyterRequest, jupyterProvisioner::provision),
                    timingService.getSchedulingOffset(request, jupyterProvisioner.getProvisioningSeconds()));
            default -> throw new IllegalStateException("Unexpected value: " + request);
        }
        return new ScheduleResponse(requestEntity.getId());
    }

    private Void scheduleCreationAndDeletion(
            ScheduleRequestEntity request,
            ThrowingFunction<VMResourceDetails, List<ActivityLinkInfo>> provisionSoftware
    ) throws InterruptedException {
        try {
            scheduleRequestTracker.updateStatus(request.getId(), VM_CREATING);
            timedVmProvider
                    .createInstance(requestMapper.toDto(request).getPsm())
                    .thenAccept(provisionSoftwareAndScheduleDeletion(request, provisionSoftware))
                    .get();
        } catch (ExecutionException e) {
            switch (e.getCause()) {
                case VMOperationFailed ex -> handleFailedVmCreation(request, ex);
                default -> handleUnexpectedException(request, e.getCause());
            }
        }
        return null;
    }

    private void handleFailedVmCreation(ScheduleRequestEntity request, VMOperationFailed ex) {
        logger.error("Error while creating instance. Request: {}", request, ex);
        scheduleRequestTracker.updateStatus(request.getId(), VM_CREATION_FAILED);
    }

    private void handleUnexpectedException(ScheduleRequestEntity request, Throwable ex) {
        logger.error("Unexpected exception. Request: {}", request, ex);
        scheduleRequestTracker.updateStatus(request.getId(), VM_CREATION_FAILED);
    }

    private Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(
            ScheduleRequestEntity request,
            ThrowingFunction<VMResourceDetails, List<ActivityLinkInfo>> provisionSoftware
    ) {
        return resourceDetails -> {
            try {
                scheduleRequestTracker.updateStatus(request.getId(), PROVISIONING);
                scheduleRequestTracker.fillVmResourceId(request.getId(), resourceDetails.internalResourceId());
                var links = CheckedExceptionConverter.from(provisionSoftware).apply(resourceDetails);
                var updatedRequest = scheduleRequestTracker.updateStatus(request.getId(), READY);
                scheduleRequestTracker.saveLinks(request.getId(), links);
                scheduler.schedule(
                        () -> deleteInstance(updatedRequest, resourceDetails),
                        timingService.getDeletionOffset(requestMapper.toDto(updatedRequest), CLEANUP_SECONDS));
            } catch (ProvisioningFailed e) {
                logger.error("Provisioning software on: {} failed. Scheduling deletion.", resourceDetails, e);
                scheduleRequestTracker.updateStatus(request.getId(), PROVISIONING_FAILED);
            } catch (Exception e) {
                scheduleRequestTracker.updateStatus(request.getId(), PROVISIONING_FAILED);
                throw e;
            }
        };
    }

    private Void deleteInstance(ScheduleRequestEntity request, VMResourceDetails connectionDetails) throws InterruptedException {
        try {
            timedVmProvider.deleteInstance(connectionDetails.internalResourceId())
                    .thenRun(() -> scheduleRequestTracker.updateStatus(request.getId(), DELETED));
        } catch (VMOperationFailed e) {
            logger.error("Deleting instance failed!", e);
        }
        return null;
    }
}
