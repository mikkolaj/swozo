package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.orchestrator.cloud.resources.vm.VMConnectionDetails;
import com.swozo.orchestrator.cloud.software.TimedSoftwareProvisioner;
import com.swozo.orchestrator.cloud.software.docker.DockerProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JupyterProvisioner implements TimedSoftwareProvisioner {
    private static final int PROVISIONING_SECONDS = 120;
    private final DockerProvisioner dockerProvisioner;

    @Override
    public boolean provision(VMConnectionDetails vmConnectionDetails) {
        dockerProvisioner.provision(vmConnectionDetails);
        return false;
    }

    @Override
    public int getProvisioningSeconds() {
        return dockerProvisioner.getProvisioningSeconds() + PROVISIONING_SECONDS;
    }
}
