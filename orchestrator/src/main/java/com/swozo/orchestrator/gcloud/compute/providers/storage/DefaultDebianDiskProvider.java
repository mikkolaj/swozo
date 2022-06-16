package com.swozo.orchestrator.gcloud.compute.providers.storage;

import com.google.cloud.compute.v1.AttachedDisk;
import com.google.cloud.compute.v1.AttachedDiskInitializeParams;

public class DefaultDebianDiskProvider implements DiskProvider {
    private final static String sourceImageTemplate = "projects/debian-cloud/global/images/family/%s";

    @Override
    public AttachedDisk createDisk(String diskName, String debianVersion, long diskSizeGb) {
        var sourceImage = String.format(sourceImageTemplate, debianVersion);
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
