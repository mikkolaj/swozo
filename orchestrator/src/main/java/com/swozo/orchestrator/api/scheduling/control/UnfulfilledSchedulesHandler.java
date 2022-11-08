package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.exceptions.PropagatingException;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class UnfulfilledSchedulesHandler implements ApplicationListener<ApplicationReadyEvent> {
    private final ScheduleRequestTracker requestTracker;
    private final ScheduleHandler scheduleHandler;
    private final TimedVMProvider vmProvider;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void onApplicationEvent(@NotNull ApplicationReadyEvent event) {
        requestTracker.getUnfulfilledSchedulesToDelete().forEach(this::deleteCreatedVm);
        requestTracker.getValidSchedulesToRestartFromBeginning().forEach(this::delegateScheduling);
        requestTracker.getValidSchedulesToReprovision().forEach(this::reprovision);
        requestTracker.getSchedulesToCleanAndDelete()
                .forEach(requestEntity -> scheduleAction(requestEntity, this::extractDetailsThenScheduleCleaningAndDeletion));
        requestTracker.getSchedulesToDelete()
                .forEach(requestEntity -> scheduleAction(requestEntity, this::extractDetailsThenScheduleDeletion));
    }

    private void deleteCreatedVm(ScheduleRequestEntity requestEntity) {
        try {
            requestEntity.getVmResourceId().ifPresent(resourceId ->
                    CheckedExceptionConverter
                            .from(scheduleHandler::deleteIfAllExported)
                            .accept(requestEntity, resourceId)
            );
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

    private void reprovision(ScheduleRequestEntity requestEntity) {
        try {
            var resourceId = requestEntity.getVmResourceId()
                    .orElseThrow(getNoRegisteredVmException(requestEntity, "reprovision"));
            CheckedExceptionConverter.from(() -> vmProvider.getVMResourceDetails(resourceId)
                    .thenAccept(extractDetailsAndReprovision(requestEntity))
                    .get()
            ).get();
        } catch (IllegalArgumentException | IllegalStateException ex) {
            logger.warn(ex.getMessage());
        } catch (RuntimeException ex) {
            handleRuntimeException(ex);
        }
    }

    private Consumer<Optional<VMResourceDetails>> extractDetailsAndReprovision(ScheduleRequestEntity requestEntity) {
        return possibleDetails -> {
            var resourceDetails = possibleDetails.orElseThrow(getNoMatchingVmException(requestEntity));
            scheduleHandler.provisionSoftwareAndScheduleDeletion(requestEntity)
                    .accept(resourceDetails);
        };
    }

    private void scheduleAction(ScheduleRequestEntity requestEntity, Function<ScheduleRequestEntity, Consumer<Optional<VMResourceDetails>>> consumerProvider) {
        try {
            var resourceId =
                    requestEntity.getVmResourceId().orElseThrow(getNoRegisteredVmException(requestEntity, "delete"));

            CheckedExceptionConverter.from(() -> vmProvider.getVMResourceDetails(resourceId)
                    .thenAccept(consumerProvider.apply(requestEntity))
                    .get()
            ).get();
        } catch (IllegalArgumentException ex) {
            logger.warn(ex.getMessage());
        } catch (RuntimeException ex) {
            handleRuntimeException(ex);
        }
    }

    private Consumer<Optional<VMResourceDetails>> extractDetailsThenScheduleCleaningAndDeletion(ScheduleRequestEntity requestEntity) {
        return possibleDetails -> {
            var resourceDetails = possibleDetails.orElseThrow(getNoMatchingVmException(requestEntity));
            scheduleHandler.scheduleExportAndDeletion(requestEntity, resourceDetails);
        };
    }

    private Consumer<Optional<VMResourceDetails>> extractDetailsThenScheduleDeletion(ScheduleRequestEntity requestEntity) {
        return possibleDetails -> {
            var resourceDetails = possibleDetails.orElseThrow(getNoMatchingVmException(requestEntity));
            scheduleHandler.scheduleDeletion(requestEntity, resourceDetails);
        };
    }

    private Supplier<IllegalArgumentException> getNoRegisteredVmException(ScheduleRequestEntity requestEntity, String action) {
        return () -> new IllegalArgumentException(String.format("Request to %s must already have a Vm created. Request: %s", action, requestEntity));
    }

    private Supplier<IllegalStateException> getNoMatchingVmException(ScheduleRequestEntity requestEntity) {
        return () -> new IllegalStateException(String.format("Can't find a VM associated to request: %s", requestEntity));
    }

    private void handleRuntimeException(RuntimeException ex) {
        if (ex instanceof PropagatingException) {
            throw ex;
        } else {
            logger.error("Exception during recovery phase.", ex);
        }
    }
}
