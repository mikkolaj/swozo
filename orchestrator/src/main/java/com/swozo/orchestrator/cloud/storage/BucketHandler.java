package com.swozo.orchestrator.cloud.storage;

import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import org.springframework.scheduling.annotation.Async;

public interface BucketHandler {
    @Async
    void uploadUsersWorkdirToBucket(
            VmResourceDetails remoteHost,
            String workdirPath,
            long activityModuleId,
            long scheduleRequestId,
            long userId
    );

    @Async
    void downloadToHost(VmResourceDetails remoteHost, String remoteFileId, String destinationPath) throws BucketOperationFailed;
}
