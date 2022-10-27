package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@RequiredArgsConstructor
// TODO: add retrying of received, but not fulfilled requests
public class UnfulfilledSchedulesHandler {
    private final ScheduleRequestTracker requestTracker;

    private final ScheduleHandler scheduleHandler;

    @PostConstruct
    public void handleMissedSchedules() {
        requestTracker.getSchedulesToDelete().forEach(this::deleteCreatedVmAndUpdateStatus);
    }

    private void deleteCreatedVmAndUpdateStatus(ScheduleRequestEntity requestEntity) {
        requestEntity.getVmResourceId().ifPresent(resourceId ->
                CheckedExceptionConverter
                        .from(scheduleHandler::deleteInstance)
                        .accept(requestEntity, resourceId)
        );
        requestTracker.updateStatus(requestEntity.getId(), RequestStatus.DELETED);
    }
}
