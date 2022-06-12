package com.swozo.orchestrator.cloud.software;

import com.swozo.model.links.Link;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;

import java.util.List;

public interface TimedSoftwareProvisioner {
    List<Link> provision(VMResourceDetails resourceDetails) throws InterruptedException;

    int getProvisioningSeconds();
}
