package com.swozo.orchestrator.cloud.storage;

import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import org.springframework.scheduling.annotation.Async;

import java.util.concurrent.CompletableFuture;

public interface BucketHandler {
    @Async
    CompletableFuture<Void> uploadUsersWorkdirToBucket(
            VMResourceDetails remoteHost,
            String workdirPath,
            long activityModuleId,
            long scheduleRequestId,
            long userId
    );

    @Async
    CompletableFuture<Void> downloadToHost(VMResourceDetails remoteHost, String remoteFileId, String destinationPath) throws BucketOperationFailed;
}
