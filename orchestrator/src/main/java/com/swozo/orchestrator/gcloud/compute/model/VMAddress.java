package com.swozo.orchestrator.gcloud.compute.model;

public record VMAddress(String project, String zone, String networkName, String vmName) {
}
