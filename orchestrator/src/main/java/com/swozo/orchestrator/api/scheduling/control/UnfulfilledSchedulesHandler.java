package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.exceptions.PropagatingException;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import static com.swozo.orchestrator.utils.CollectionUtils.combineLists;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class UnfulfilledSchedulesHandler implements ApplicationListener<ApplicationReadyEvent> {
    private final ScheduleRequestTracker requestTracker;
    private final ScheduleHandler scheduleHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        var outdatedSchedules = requestTracker.getOutdatedSchedulesWithoutVm();
        var schedulesToDelete = requestTracker.getSchedulesToDelete();
        var schedulesToRestart = requestTracker.getValidSchedulesToRestartFromBeginning();
        var withVmBeforeExport = requestTracker.getSchedulesWithVmBeforeExport();
        logger.info("Outdated Vms: {}", outdatedSchedules);
        logger.info("To delete: {}", schedulesToDelete);
        logger.info("To restart: {}", schedulesToRestart);
        logger.info("Living vms waiting for export: {}", withVmBeforeExport);
        outdatedSchedules.forEach(this::setFailedStatus);
        schedulesToDelete.forEach(this::deleteCreatedVm);
        combineLists(schedulesToRestart, withVmBeforeExport).forEach(this::continueProvisioningFlow);
    }

    private void setFailedStatus(ScheduleRequestEntity request) {
        requestTracker.updateStatus(request, ServiceStatus.FAILED);
    }

    private void deleteCreatedVm(ScheduleRequestEntity requestEntity) {
        try {
            scheduleHandler.scheduleConditionalDeletion(requestEntity);
        } catch (RuntimeException ex) {
            handleRuntimeException(ex);
        }
    }

    private void continueProvisioningFlow(ScheduleRequestEntity requestEntity) {
        try {
            scheduleHandler.continueSchedulingFlowAfterFailure(requestEntity);
        } catch (RuntimeException ex) {
            handleRuntimeException(ex);
        }
    }

    private void handleRuntimeException(RuntimeException ex) {
        if (ex instanceof PropagatingException) {
            throw ex;
        } else {
            logger.error("Exception during recovery phase.", ex);
        }
    }
}
