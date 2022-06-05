package com.swozo.orchestrator.cloud.software.docker;

import com.swozo.orchestrator.cloud.resources.vm.VMConnectionDetails;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import org.springframework.stereotype.Service;

@Service
public class DockerProvisioner implements TimedSoftwareProvisioner {
    private static final int PROVISIONING_SECONDS = 120;

    @Override
    public boolean provision(VMConnectionDetails vmConnectionDetails) {
        return false;
    }

    @Override
    public int getProvisioningSeconds() {
        return PROVISIONING_SECONDS;
    }
}
