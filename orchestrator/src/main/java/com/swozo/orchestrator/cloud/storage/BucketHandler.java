package com.swozo.orchestrator.cloud.storage;

import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;

import java.util.concurrent.CompletableFuture;

public interface BucketHandler {
    CompletableFuture<Void> uploadUsersWorkdirToBucket(
            VmResourceDetails remoteHost,
            String workdirPath,
            long activityModuleId,
            long scheduleRequestId,
            long userId
    );

    CompletableFuture<Void> downloadToHost(
            VmResourceDetails remoteHost,
            String remoteFileId,
            String destinationPath,
            String fileOwner
    ) throws BucketOperationFailed;
}
