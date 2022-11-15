package com.swozo.orchestrator.cloud.resources.vm;

public class VmOperationFailed extends RuntimeException {
    public VmOperationFailed(String message) {
        super(message);
    }

    public VmOperationFailed(Throwable cause) {
        super(cause);
    }
}
