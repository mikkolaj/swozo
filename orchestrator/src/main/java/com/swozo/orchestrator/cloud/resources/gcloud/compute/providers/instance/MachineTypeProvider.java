package com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance;

import org.springframework.stereotype.Service;

@Service
public class MachineTypeProvider {
    private static final String MACHINE_TYPE_TEMPLATE = "zones/%s/machineTypes/%s";

    public String constructMachineType(String zone, String machineType) {
        return String.format(MACHINE_TYPE_TEMPLATE, zone, machineType);
    }
}
