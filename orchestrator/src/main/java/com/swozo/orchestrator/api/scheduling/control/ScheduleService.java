package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.function.ThrowingFunction;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.JupyterScheduleRequest;
import com.swozo.model.scheduling.ScheduleRequest;
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

@Service
@RequiredArgsConstructor
public class ScheduleService {
    // TODO: probably will change if we decide to execute tasks (e.g. saving user's files) before deleting the instance
    private static final int CLEANUP_SECONDS = 0;
    private static final int IMMEDIATE_OFFSET = 0;
    private final InternalTaskScheduler scheduler;
    private final TimedVMProvider timedVmProvider;
    private final TimedSoftwareProvisioner jupyterProvisioner;
    private final ScheduleRequestTracker scheduleRequestTracker;
    private final TimingService timingService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public long schedule(ScheduleRequest request) {
        var requestId = scheduleRequestTracker.persist(request).getId();
        switch (request) {
            case JupyterScheduleRequest jupyterRequest -> scheduler.schedule(
                    () -> scheduleCreationAndDeletion(new ScheduleRequestWithId(jupyterRequest, requestId), jupyterProvisioner::provision),
                    timingService.getSchedulingOffset(request, jupyterProvisioner.getProvisioningSeconds()));
            default -> throw new IllegalStateException("Unexpected value: " + request);
        }
        return requestId;
    }

    private Void scheduleCreationAndDeletion(
            ScheduleRequestWithId requestWithId,
            ThrowingFunction<VMResourceDetails, List<ActivityLinkInfo>> provisionSoftware
    ) throws InterruptedException {
        try {
            timedVmProvider
                    .createInstance(requestWithId.getPsm())
                    .thenAccept(provisionSoftwareAndScheduleDeletion(requestWithId, provisionSoftware))
                    .get();
        } catch (ExecutionException e) {
            switch (e.getCause()) {
                case VMOperationFailed ex ->
                        logger.error("Error while creating instance. Request: {}", requestWithId, ex);
                default -> logger.error("Unexpected exception. Request: {}", requestWithId, e);
            }
        }
        return null;
    }

    private Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(
            ScheduleRequestWithId requestWithId,
            ThrowingFunction<VMResourceDetails, List<ActivityLinkInfo>> provisionSoftware
    ) {
        return resourceDetails -> {
            try {
                var links = CheckedExceptionConverter.from(provisionSoftware).apply(resourceDetails);
                scheduleRequestTracker.saveLinks(links, requestWithId.requestId());
                scheduler.schedule(
                        () -> deleteInstance(requestWithId, resourceDetails),
                        timingService.getDeletionOffset(requestWithId.request(), CLEANUP_SECONDS));
            } catch (ProvisioningFailed e) {
                logger.error("Provisioning software on: {} failed. Scheduling deletion.", resourceDetails, e);
                scheduler.schedule(() -> deleteInstance(requestWithId, resourceDetails), IMMEDIATE_OFFSET);
            }
        };
    }

    private Void deleteInstance(ScheduleRequestWithId requestWithId, VMResourceDetails connectionDetails) throws InterruptedException {
        try {
            timedVmProvider.deleteInstance(connectionDetails.internalResourceId())
                    .thenRun(() -> scheduleRequestTracker.unpersist(requestWithId.requestId()));
        } catch (VMOperationFailed e) {
            logger.error("Deleting instance failed!", e);
        }
        return null;
    }
}
