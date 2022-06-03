package com.swozo.orchestrator.cloud.resources.gcloud.compute.model;

public record VMSpecs(String machineType, String imageFamily, long diskSizeGB) {
}
