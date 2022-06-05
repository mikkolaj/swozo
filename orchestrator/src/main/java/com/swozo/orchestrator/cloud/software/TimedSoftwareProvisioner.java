package com.swozo.orchestrator.cloud.software;

import com.swozo.orchestrator.cloud.resources.vm.VMConnectionDetails;

public interface TimedSoftwareProvisioner {
    boolean provision(VMConnectionDetails connectionDetails);
    int getProvisioningSeconds();
}
