package com.swozo.orchestrator.cloud.software.runner.process;

public class ProcessFailed extends RuntimeException {
    public ProcessFailed(String message) {
        super(message);
    }

    public ProcessFailed(Throwable cause) {
        super(cause);
    }
}
