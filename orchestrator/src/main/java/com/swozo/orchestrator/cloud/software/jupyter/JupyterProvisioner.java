package com.swozo.orchestrator.cloud.software.jupyter;

import com.swozo.orchestrator.cloud.resources.vm.VMConnectionDetails;
import com.swozo.orchestrator.cloud.software.docker.DockerProvisioner;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JupyterProvisioner {
    private final DockerProvisioner dockerProvisioner;

    public boolean provision(VMConnectionDetails vmConnectionDetails) {
        dockerProvisioner.provision(vmConnectionDetails);
        return false;
    }
}
