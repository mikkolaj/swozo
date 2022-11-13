package com.swozo.orchestrator.cloud.storage;

public class BucketOperationFailed extends RuntimeException {
    public BucketOperationFailed(Throwable ex) {
        super(ex);
    }
}
