package com.swozo.orchestrator.cloud.resources.vm;

public class VMOperationFailed extends Exception {
    public VMOperationFailed(String message) {
        super(message);
    }

    public VMOperationFailed(Throwable cause) {
        super(cause);
    }
}