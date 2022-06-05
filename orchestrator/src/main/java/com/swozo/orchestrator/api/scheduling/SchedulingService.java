package com.swozo.orchestrator.api.scheduling;

import com.swozo.model.scheduling.ScheduleJupyter;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.cloud.resources.vm.VMConnectionDetails;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VMProvider;
import com.swozo.orchestrator.cloud.software.jupyter.JupyterProvisioner;
import com.swozo.orchestrator.scheduler.TaskScheduler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class SchedulingService {
    private final TaskScheduler scheduler;
    private final VMProvider vmProvider;
    private final JupyterProvisioner jupyterProvisioner;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void schedule(ScheduleRequest request) {
        switch (request) {
            case ScheduleJupyter jupyterRequest ->
                    scheduler.schedule(() -> scheduleCreationAndDeletion(jupyterRequest, jupyterProvisioner::provision), 1);
            default -> throw new IllegalArgumentException("Unsupported request type: " + request);
        }
    }

    private Void scheduleCreationAndDeletion(ScheduleRequest scheduleRequest, Consumer<VMConnectionDetails> provisionSoftware) throws InterruptedException {
        try {
            var futureAddress = vmProvider.createInstance(scheduleRequest.getPsm());
            futureAddress.thenAccept(vmConnectionDetails -> {
                provisionSoftware.accept(vmConnectionDetails);
                scheduler.schedule(() -> deleteInstance(vmConnectionDetails), 3600);
            });
        } catch (VMOperationFailed e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    private Void deleteInstance(VMConnectionDetails connectionDetails) throws InterruptedException {
        try {
            vmProvider.deleteInstance(connectionDetails.internalResourceId());
        } catch (VMOperationFailed e) {
            logger.error(e.getMessage());
        }
        return null;
    }
}
