package com.swozo.orchestrator.cloud.software.runner;

public class NotebookFailed extends RuntimeException {
    public NotebookFailed(String message) {
        super(message);
    }

    public NotebookFailed(Throwable cause) {
        super(cause);
    }
}
