package com.swozo.orchestrator.cloud.software;

import com.swozo.model.users.ActivityRole;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.control.ScheduleRequestTracker;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.cloud.storage.BucketHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus.EXPORT_FAILED;

@Service
@RequiredArgsConstructor
public class WorkspaceExporter {
    private final BackendRequestSender requestSender;
    private final BucketHandler bucketHandler;
    private final ScheduleRequestTracker requestTracker;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    public void exportToBucket(
            VmResourceDetails resourceDetails,
            String workdirPath,
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity serviceDescription
    ) {
        requestTracker.updateStatus(serviceDescription, ServiceStatus.EXPORTING);
        var usersDetails =
                requestSender.getUserData(serviceDescription.getActivityModuleId(), requestEntity.getId());
        usersDetails.thenCompose(details -> {
            assertDetailsNotEmpty(details, requestEntity, serviceDescription);
            var owner = extractOwner(details);
            return bucketHandler.uploadUsersWorkdirToBucket(
                    resourceDetails,
                    workdirPath,
                    serviceDescription.getActivityModuleId(),
                    requestEntity.getId(),
                    owner.id()
            );
        }).whenComplete((msg, ex) -> {
            if (ex != null) {
                requestTracker.updateStatus(serviceDescription, EXPORT_FAILED);
                logger.error("Failure when saving {} on {}", workdirPath, resourceDetails, ex);
            } else {
                logger.info("Successful export for request [id: {}] from {}", requestEntity.getId(), resourceDetails);
                requestTracker.updateStatus(serviceDescription, ServiceStatus.EXPORT_COMPLETE);
            }
        });
    }

    private OrchestratorUserDto extractOwner(List<OrchestratorUserDto> details) {
        if (details.size() == 1) {
            return details.get(0);
        } else {
            return details.stream().filter(user -> user.role() == ActivityRole.TEACHER).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Invalid user details response for shared service, activity should contain a teacher"));
        }
    }

    private void assertDetailsNotEmpty(
            List<OrchestratorUserDto> details, ScheduleRequestEntity scheduleRequest, ServiceDescriptionEntity serviceDescription
    ) {
        if (details.isEmpty()) {
            throw new IllegalStateException(String.format(
                    "Got empty user details for schedule request: %s, activityModuleId: %s",
                    scheduleRequest.getId(), serviceDescription.getActivityModuleId()
            ));
        }
    }
}
