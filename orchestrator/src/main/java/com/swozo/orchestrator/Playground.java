package com.swozo.orchestrator;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.GCloudVMLifecycleManager;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Playground implements Runnable {
    private static final String project = "hybrid-text-350213";
    private static final String zone = "europe-central2-a";
    private static final String networkName = "default";
    private static final String vmName = "super-instancja";
    private static final String machineType = "e2-medium";
    private static final String imageFamily = "debian-11";
    private static final int diskSizeGb = 10;
    private final GCloudVMLifecycleManager gCloudVmLifecycleManager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public void createInstance() {
        var vmAddress = new VMAddress(project, zone, networkName, vmName);
        var vmSpecs = new VMSpecs(machineType, imageFamily, diskSizeGb);

        try {
            gCloudVmLifecycleManager.createInstance(vmAddress, vmSpecs);
        } catch (Exception exception) {
            logger.error("Failed to create instance. {}. Exception: {}", vmAddress, exception);
        }
    }

    public void deleteInstance() {
        var vmAddress = new VMAddress(project, zone, networkName, vmName);

        try {
            gCloudVmLifecycleManager.deleteInstance(vmAddress);
        } catch (Exception exception) {
            logger.error("Failed to delete instance. {}. Exception: {}", vmAddress, exception);
        }
    }

    @Override
    public void run() {
//        createInstance();
//        deleteInstance();
    }
}
