package com.swozo.orchestrator.cloud.software;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;

import java.util.List;

public interface TimedSoftwareProvisioner {
    List<ActivityLinkInfo> provision(VMResourceDetails resourceDetails) throws InterruptedException, ProvisioningFailed;

    int getProvisioningSeconds();
}
