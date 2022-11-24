package com.swozo.orchestrator.cloud.resources.vm;

public class ResourceNoLongerExists extends RuntimeException {
    public ResourceNoLongerExists(String message) {
        super(message);
    }

    public ResourceNoLongerExists(Throwable cause) {
        super(cause);
    }
}
