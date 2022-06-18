package com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage;

import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.AttachedDiskInitializeParams;

public class DefaultDebianDiskProvider implements DiskProvider {
    private static final String SOURCE_IMAGE_TEMPLATE = "projects/debian-cloud/global/images/family/%s";

    @Override
    public AttachedDisk createDisk(String diskName, String debianVersion, long diskSizeGb) {
        var sourceImage = String.format(SOURCE_IMAGE_TEMPLATE, debianVersion);
        var diskParameters = AttachedDiskInitializeParams.newBuilder()
                .setSourceImage(sourceImage)
                .setDiskSizeGb(diskSizeGb)
                .build();

        return AttachedDisk.newBuilder()
                .setBoot(true)
                .setAutoDelete(true)
                .setType(AttachedDisk.Type.PERSISTENT.toString())
                .setDeviceName(diskName)
                .setInitializeParams(diskParameters)
                .build();
    }
}
