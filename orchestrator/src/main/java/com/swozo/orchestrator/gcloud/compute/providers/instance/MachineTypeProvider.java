package com.swozo.orchestrator.gcloud.compute.providers.instance;

import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

@Service
public class MachineTypeProvider {
    private static final String machineTypeTemplate = "zones/%s/machineTypes/%s";

    public String constructMachineType(String zone, String machineType) {
        return String.format(machineTypeTemplate, zone, machineType);
    }
}
