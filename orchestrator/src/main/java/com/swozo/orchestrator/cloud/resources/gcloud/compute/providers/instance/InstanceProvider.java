package com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance;

import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.NetworkInterface;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;

public interface InstanceProvider {
    Instance createInstance(VMAddress vmAddress, VMSpecs vmSpecs, AttachedDisk disk, NetworkInterface networkInterface);
}
