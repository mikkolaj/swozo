package com.swozo.orchestrator.api.scheduling.control.recovery;

import com.swozo.orchestrator.api.scheduling.control.ScheduleHandler;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReschedulingManager {
    private static final long DELAY_BETWEEN_RESCHEDULING = 1000L * 60 * 5;
    private final FailedSchedulesFinder finder;
    private final ScheduleHandler scheduleHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Scheduled(initialDelay = DELAY_BETWEEN_RESCHEDULING, fixedDelay = DELAY_BETWEEN_RESCHEDULING)
    public void periodicallyReschedule() {
        finder.getFailedDuringVmCreation().forEach(this::tryDelegatingScheduling);
        finder.getFailedSchedulesWithVm().forEach(this::tryContinuingSchedulingFlow);
    }

    private void tryDelegatingScheduling(ScheduleRequestEntity requestEntity) {
        try {
            logger.info("Delegating scheduling for request [id: {}].", requestEntity.getId());
            scheduleHandler.delegateScheduling(requestEntity);
        } catch (RuntimeException ex) {
            logger.error("Failure during rescheduling", ex);
        }
    }

    private void tryContinuingSchedulingFlow(ScheduleRequestEntity requestEntity, FailedSchedulesFinder.ServicesToRetry servicesToRetry) {
        try {
            logger.info(
                    "Rescheduling request [id: {}]. Services to reprovision: {}. Services to export: {}",
                    requestEntity.getId(),
                    servicesToRetry.toReprovision(),
                    servicesToRetry.toExport()
            );
            requestEntity.getVmResourceId().ifPresent(id ->
                    // TODO: rescheduling might lead to accumulation of uncancellable deletion tasks, possible (but very unlikely) OOM ;)
                    scheduleHandler.continueProvisioningWithPresentVm(
                            requestEntity,
                            servicesToRetry.toReprovision(),
                            servicesToRetry.toExport(),
                            id
                    )
            );
        } catch (RuntimeException ex) {
            logger.error("Failure during rescheduling", ex);
        }
    }

}
