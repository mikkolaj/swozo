package com.swozo.orchestrator.configuration;

public enum CloudProvider {
    GCLOUD;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
