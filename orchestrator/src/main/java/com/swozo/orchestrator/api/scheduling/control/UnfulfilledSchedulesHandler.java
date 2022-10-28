package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class UnfulfilledSchedulesHandler implements ApplicationListener<ApplicationPreparedEvent> {
    private final ScheduleRequestTracker requestTracker;
    private final ScheduleHandler scheduleHandler;
    private final TimedVMProvider vmProvider;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        requestTracker.getSchedulesToDelete().forEach(this::deleteCreatedVm);
        requestTracker.getValidSchedulesToRestartFromBeginning().forEach(scheduleHandler::delegateScheduling);
        requestTracker.getValidSchedulesToReprovision().forEach(this::reprovision);
        requestTracker.getValidReadySchedules().forEach(this::scheduleDeletion);
    }

    private void deleteCreatedVm(ScheduleRequestEntity requestEntity) {
        requestEntity.getVmResourceId().ifPresent(resourceId ->
                CheckedExceptionConverter
                        .from(scheduleHandler::deleteInstance)
                        .accept(requestEntity, resourceId)
        );
    }

    private void reprovision(ScheduleRequestEntity requestEntity) {
        try {
            var resourceId = requestEntity.getVmResourceId()
                    .orElseThrow(getNoRegisteredVmException(requestEntity, "reprovision"));
            vmProvider.getVMResourceDetails(resourceId)
                    .thenAccept(extractDetailsAndReprovision(requestEntity));
        } catch (IllegalArgumentException | IllegalStateException ex) {
            logger.warn(ex.getMessage());
        }
    }

    private Consumer<Optional<VMResourceDetails>> extractDetailsAndReprovision(ScheduleRequestEntity requestEntity) {
        return possibleDetails -> {
            var resourceDetails = possibleDetails.orElseThrow(getNoMatchingVmException(requestEntity));
            scheduleHandler.provisionSoftwareAndScheduleDeletion(requestEntity).accept(resourceDetails);
        };
    }

    private void scheduleDeletion(ScheduleRequestEntity requestEntity) {
        try {
            var resourceId =
                    requestEntity.getVmResourceId().orElseThrow(getNoRegisteredVmException(requestEntity, "delete"));
            scheduleHandler.scheduleInstanceDeletion(requestEntity, resourceId);
        } catch (IllegalArgumentException ex) {
            logger.warn(ex.getMessage());
        }
    }

    private Supplier<IllegalArgumentException> getNoRegisteredVmException(ScheduleRequestEntity requestEntity, String action) {
        return () -> new IllegalArgumentException(String.format("Request to %s must already have a Vm created. Request: %s", action, requestEntity));
    }

    private Supplier<IllegalStateException> getNoMatchingVmException(ScheduleRequestEntity requestEntity) {
        return () -> new IllegalStateException(String.format("Can't find a VM associated to request: %s", requestEntity));
    }
}
