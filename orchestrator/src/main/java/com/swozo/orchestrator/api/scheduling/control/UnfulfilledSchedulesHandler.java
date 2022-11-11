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

@Component
@Profile("!test")
@RequiredArgsConstructor
public class UnfulfilledSchedulesHandler implements ApplicationListener<ApplicationReadyEvent> {
    private final ScheduleRequestTracker requestTracker;
    private final ScheduleHandler scheduleHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        requestTracker.getOutdatedSchedulesWithoutVm()
                .forEach(request -> requestTracker.updateStatus(request, ServiceStatus.FAILED));
        var schedulesToDelete = requestTracker.getSchedulesToDelete();
        var schedulesToRestart = requestTracker.getValidSchedulesToRestartFromBeginning();
        var withVmBeforeExport = requestTracker.getSchedulesWithVmBeforeExport();
        logger.info("To delete: {}", schedulesToDelete);
        logger.info("To restart: {}", schedulesToRestart);
        logger.info("To withVm: {}", withVmBeforeExport);
        schedulesToDelete.forEach(this::deleteCreatedVm);
        schedulesToRestart.forEach(this::delegateScheduling);
        withVmBeforeExport.forEach(this::applyActionsToServices);
    }

    private void deleteCreatedVm(ScheduleRequestEntity requestEntity) {
        try {
            scheduleHandler.scheduleConditionalDeletion(requestEntity);
        } catch (RuntimeException ex) {
            handleRuntimeException(ex);
        }
    }

    private void delegateScheduling(ScheduleRequestEntity requestEntity) {
        try {
            scheduleHandler.delegateScheduling(requestEntity);
        } catch (RuntimeException ex) {
            handleRuntimeException(ex);
        }
    }

    private void applyActionsToServices(ScheduleRequestEntity requestEntity) {
        try {
            scheduleHandler.applyAppropriateActionsOnServices(requestEntity);
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
