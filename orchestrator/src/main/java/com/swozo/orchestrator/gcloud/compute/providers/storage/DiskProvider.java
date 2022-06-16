package com.swozo.orchestrator.gcloud.compute.providers.storage;

import com.google.cloud.compute.v1.AttachedDisk;

public interface DiskProvider {
    AttachedDisk createDisk(String diskName, String imageFamily, long diskSizeGb);
}
