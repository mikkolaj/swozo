package com.swozo.orchestrator.cloud.software;

public class ProvisioningFailed extends Exception {
    public ProvisioningFailed(String message) {
        super(message);
    }

    public ProvisioningFailed(Throwable cause) {
        super(cause);
    }
}
