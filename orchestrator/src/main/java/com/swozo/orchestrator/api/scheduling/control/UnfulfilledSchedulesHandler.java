package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
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
    private final TimedVMProvider vmProvider;

    @PostConstruct
    public void handleMissedSchedules() {
        requestTracker.getSchedulesToDelete().forEach(this::deleteCreatedVmAndUpdateStatus);
        requestTracker.getValidSchedulesToRestartFromBeginning().forEach(scheduleHandler::delegateScheduling);
        requestTracker.getValidSchedulesToReprovision().forEach(this::reprovision);
    }

    private void deleteCreatedVmAndUpdateStatus(ScheduleRequestEntity requestEntity) {
        requestEntity.getVmResourceId().ifPresent(resourceId ->
                CheckedExceptionConverter
                        .from(scheduleHandler::deleteInstance)
                        .accept(requestEntity, resourceId)
        );
        requestTracker.updateStatus(requestEntity.getId(), RequestStatus.DELETED);
    }

    private void reprovision(ScheduleRequestEntity requestEntity) {
        requestEntity.getVmResourceId().ifPresent(resourceId ->
                vmProvider.getVMResourceDetails(resourceId)
                        .thenAccept(possibleDetails -> possibleDetails.ifPresent(details ->
                                scheduleHandler.provisionSoftwareAndScheduleDeletion(requestEntity)
                                        .accept(details)
                        ))
        );
    }
}
