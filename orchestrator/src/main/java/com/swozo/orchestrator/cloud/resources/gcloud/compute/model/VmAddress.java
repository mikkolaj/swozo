package com.swozo.orchestrator.cloud.resources.gcloud.compute.model;

public record VmAddress(String project, String zone, String networkName, String vmName) {
}
