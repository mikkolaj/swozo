package com.swozo.orchestrator.cloud.software.docker;

import com.swozo.orchestrator.cloud.resources.vm.VMConnectionDetails;
import org.springframework.stereotype.Service;

@Service
public class DockerProvisioner {

    public boolean provision(VMConnectionDetails vmConnectionDetails) {
        return false;
    }
}
