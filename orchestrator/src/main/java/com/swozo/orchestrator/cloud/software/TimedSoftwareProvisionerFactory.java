package com.swozo.orchestrator.cloud.software;

import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.orchestrator.cloud.software.jupyter.JupyterProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimedSoftwareProvisionerFactory {
    private final JupyterProvisioner jupyterProvisioner;

    public TimedSoftwareProvisioner getProvisioner(ScheduleType type) {
        return switch (type) {
            case JUPYTER -> jupyterProvisioner;
            case DOCKER -> throw new IllegalStateException("Docker provisioner not yet implemented!");
        };
    }
}
