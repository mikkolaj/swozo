package com.swozo.orchestrator.cloud.software;

import com.swozo.model.users.ActivityRole;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
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

@Service
@RequiredArgsConstructor
public class WorkspaceExporter {
    private final BackendRequestSender requestSender;
    private final BucketHandler bucketHandler;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    public void exportToBucket(
            VmResourceDetails resourceDetails,
            String workdirPath,
            ScheduleRequestEntity requestEntity,
            ServiceDescriptionEntity serviceDescription
    ) {
        serviceDescription.setStatus(ServiceStatus.EXPORTING);
        var usersDetails =
                requestSender.getUserData(requestEntity.getId(), serviceDescription.getActivityModuleId());
        usersDetails.thenAccept(details -> {
            var owner = extractOwner(details);
            bucketHandler.uploadUsersWorkdirToBucket(resourceDetails, workdirPath, requestEntity.getId(), serviceDescription.getActivityModuleId(), owner.id());
        }).whenComplete((msg, ex) -> {
            if (ex != null) {
                serviceDescription.setStatus(ServiceStatus.EXPORT_FAILED);
                logger.error("Failure when saving {} on {}", workdirPath, resourceDetails, ex);
            } else {
                serviceDescription.setStatus(ServiceStatus.EXPORT_COMPLETE);
            }
        });
    }

    private OrchestratorUserDto extractOwner(List<OrchestratorUserDto> details) {
        if (details.size() == 1) {
            return details.get(0);
        } else {
            return details.stream().filter(user -> user.role() == ActivityRole.TEACHER).findFirst()
                    .orElseThrow(() -> new IllegalStateException("Invalid user details backend, activity should contain a teacher"));
        }
    }

}
