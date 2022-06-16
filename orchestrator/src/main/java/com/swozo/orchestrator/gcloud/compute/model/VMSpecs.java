package com.swozo.orchestrator.gcloud.compute.model;

public record VMSpecs(String machineType, String imageFamily, long diskSizeGB) {
}
