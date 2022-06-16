package com.swozo.orchestrator.api.scheduling;

import com.swozo.function.ThrowingFunction;
import com.swozo.model.links.Link;
import com.swozo.model.scheduling.ScheduleJupyter;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.scheduler.TaskScheduler;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final TaskScheduler scheduler;
    private final TimedVMProvider timedVmProvider;
    private final TimedSoftwareProvisioner jupyterProvisioner;
    private final ScheduleRequestTracker scheduleRequestTracker;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void schedule(ScheduleRequest request) {
        scheduleRequestTracker.persist(request);
        switch (request) {
            case ScheduleJupyter scheduleJupyter -> scheduler.schedule(() -> scheduleCreationAndDeletion(scheduleJupyter, jupyterProvisioner::provision), 1);
            default -> throw new IllegalStateException("Unexpected request type: " + request);
        }
    }

    private Void scheduleCreationAndDeletion(
            ScheduleRequest scheduleRequest,
            ThrowingFunction<VMResourceDetails, List<Link>> provisionSoftware
    ) throws InterruptedException {
        try {
            timedVmProvider
                    .createInstance(scheduleRequest.getPsm())
                    .thenAccept(provisionSoftwareAndScheduleDeletion(scheduleRequest, provisionSoftware));
        } catch (VMOperationFailed e) {
            logger.error("Creating instance failed!", e);
        }
        return null;
    }

    private Consumer<VMResourceDetails> provisionSoftwareAndScheduleDeletion(
            ScheduleRequest scheduleRequest,
            ThrowingFunction<VMResourceDetails, List<Link>> provisionSoftware
    ) {
        return resourceDetails -> {
            var links = CheckedExceptionConverter.from(provisionSoftware).apply(resourceDetails);
            scheduleRequestTracker.saveLinks(scheduleRequest.getActivityModuleID(), links);
            scheduler.schedule(() -> deleteInstance(scheduleRequest, resourceDetails), 600);
        };
    }

    private Void deleteInstance(ScheduleRequest scheduleRequest, VMResourceDetails connectionDetails) throws InterruptedException {
        try {
            timedVmProvider.deleteInstance(connectionDetails.internalResourceId())
                    .thenRun(() ->scheduleRequestTracker.unpersist(scheduleRequest.getActivityModuleID()));
        } catch (VMOperationFailed e) {
            logger.error("Deleting instance failed!", e);
        }
        return null;
    }
}
