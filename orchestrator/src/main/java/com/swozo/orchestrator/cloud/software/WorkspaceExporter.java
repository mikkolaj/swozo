package com.swozo.orchestrator.cloud.software;

import com.swozo.model.users.ActivityRole;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.control.ScheduleRequestTracker;
import com.swozo.orchestrator.api.scheduling.control.helpers.AbortHandler;
import com.swozo.orchestrator.api.scheduling.control.helpers.CancelledScheduleException;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.cloud.storage.BucketHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus.EXPORT_FAILED;
import static com.swozo.utils.LoggingUtils.log;

@Service
@RequiredArgsConstructor
public class WorkspaceExporter {
    private final BackendRequestSender requestSender;
    private final BucketHandler bucketHandler;
    private final AbortHandler abortHandler;
    private final ScheduleRequestTracker requestTracker;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public CompletableFuture<Void> exportToBucket(
            VmResourceDetails resourceDetails,
            String workdirPath,
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity serviceDescription
    ) {
        return CompletableFuture.runAsync(() -> {
                            abortHandler.abortIfNecessary(serviceDescription.getId());
                            requestTracker.updateStatus(serviceDescription, ServiceStatus.EXPORTING);
                        }
                ).thenCompose(
                        x -> requestSender.getUserData(serviceDescription.getActivityModuleId(), requestEntity.getId())
                ).thenCompose(userDetails -> {
                    var owner = extractOwner(userDetails);
                    return bucketHandler.uploadUsersWorkdirToBucket(
                            resourceDetails,
                            workdirPath,
                            serviceDescription.getActivityModuleId(),
                            requestEntity.getId(),
                            owner.id()
                    );
                }).whenComplete(log(
                        logger,
                        String.format("Successful export for request [id: %s] from %s", requestEntity.getId(), resourceDetails),
                        String.format("Failure when saving %s on %s", workdirPath, resourceDetails),
                        CancelledScheduleException.class
                ))
                .whenComplete(setCorrectStatus(serviceDescription));
    }

    private BiConsumer<Void, Throwable> setCorrectStatus(ServiceDescriptionEntity serviceDescription) {
        return (msg, ex) -> {
            if (ex != null && !(ex.getCause() instanceof CancelledScheduleException)) {
                requestTracker.updateStatus(serviceDescription, EXPORT_FAILED);
            } else if (ex == null && !requestTracker.serviceWasCancelled(serviceDescription.getId())) {
                requestTracker.updateStatus(serviceDescription, ServiceStatus.EXPORT_COMPLETE);
            }
        };
    }

    private OrchestratorUserDto extractOwner(List<OrchestratorUserDto> users) {
        if (users.isEmpty()) {
            throw new IllegalStateException("Users' details response is empty. Can't save progress.");
        } else if (users.size() == 1) {
            return users.get(0);
        } else {
            return users.stream().filter(user -> user.role() == ActivityRole.TEACHER).findFirst()
                    .orElseThrow(() -> new IllegalStateException(String.format(
                            "Invalid user details response [%s] for shared service, activity should contain a teacher",
                            users
                    )));
        }
    }
}
