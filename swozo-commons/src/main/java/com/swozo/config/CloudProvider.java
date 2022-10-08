package com.swozo.config;

public enum CloudProvider {
    GCLOUD;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
