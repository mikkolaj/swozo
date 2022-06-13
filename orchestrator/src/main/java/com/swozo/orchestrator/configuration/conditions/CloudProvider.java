package com.swozo.orchestrator.configuration.conditions;

public enum CloudProvider {
    GCLOUD;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
