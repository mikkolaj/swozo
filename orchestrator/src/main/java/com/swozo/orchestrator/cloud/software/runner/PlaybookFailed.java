package com.swozo.orchestrator.cloud.software.runner;

public class PlaybookFailed extends RuntimeException {
    public PlaybookFailed(String message) {
        super(message);
    }

    public PlaybookFailed(Throwable cause) {
        super(cause);
    }
}
