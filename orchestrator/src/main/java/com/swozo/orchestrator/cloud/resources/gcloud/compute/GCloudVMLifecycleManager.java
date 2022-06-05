package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.google.cloud.compute.v1.*;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DiskProvider;
import com.swozo.orchestrator.configuration.EnvNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class GCloudVMLifecycleManager {
    private static final String DEFAULT_DISK_NAME = "disk-1";
    private static final int DEFAULT_NIC_INDEX = 0;
    private static final int DEFAULT_ACCESS_CONFIG_INDEX = 0;

    private final int gCloudTimeoutMinutes;
    private final DiskProvider diskProvider;
    private final NetworkInterfaceProvider networkInterfaceProvider;
    private final InstanceProvider instanceProvider;
    private final Logger logger = LoggerFactory.getLogger(GCloudVMLifecycleManager.class);

    @Autowired
    public GCloudVMLifecycleManager(
            DiskProvider diskProvider,
            NetworkInterfaceProvider networkInterfaceProvider,
            InstanceProvider instanceProvider,
            @Value("${" + EnvNames.GCP_REQUEST_TIMEOUT + "}") int gCloudTimeoutMinutes) {
        this.gCloudTimeoutMinutes = gCloudTimeoutMinutes;
        this.diskProvider = diskProvider;
        this.networkInterfaceProvider = networkInterfaceProvider;
        this.instanceProvider = instanceProvider;
    }

    public Operation createInstance(VMAddress vmAddress, VMSpecs vmSpecs)
            throws IOException, ExecutionException, TimeoutException, InterruptedException {
        try (InstancesClient instancesClient = InstancesClient.create()) {
            var disk =
                    diskProvider.createDisk(DEFAULT_DISK_NAME, vmSpecs.imageFamily(), vmSpecs.diskSizeGB());

            var networkInterface =
                    networkInterfaceProvider.createNetworkInterface(vmAddress.networkName());

            var instanceResource =
                    instanceProvider.createInstance(vmAddress, vmSpecs, disk, networkInterface);

            var insertInstanceRequest = createInsertInstanceRequest(vmAddress, instanceResource);

            logger.info("Creating instance: {} at {}\n", vmAddress.vmName(), vmAddress.zone());

            var futureOperation = instancesClient.insertAsync(insertInstanceRequest);

            return futureOperation.get(gCloudTimeoutMinutes, TimeUnit.MINUTES);
        }
    }

    public Operation deleteInstance(VMAddress vmAddress)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (var instancesClient = InstancesClient.create()) {
            var deleteInstanceRequest = createDeleteInstanceRequest(vmAddress);

            logger.info("Deleting instance: {} ", vmAddress.vmName());

            var futureOperation = instancesClient.deleteAsync(deleteInstanceRequest);

            return futureOperation.get(gCloudTimeoutMinutes, TimeUnit.MINUTES);
        }
    }

    public String getInstanceExternalIP(VMAddress vmAddress) throws IOException {
        try (var instancesClient = InstancesClient.create()) {
            return instancesClient.get(vmAddress.project(), vmAddress.zone(), vmAddress.vmName())
                    .getNetworkInterfaces(DEFAULT_NIC_INDEX)
                    .getAccessConfigs(DEFAULT_ACCESS_CONFIG_INDEX)
                    .getNatIP();
        }
    }

    private DeleteInstanceRequest createDeleteInstanceRequest(VMAddress vmAddress) {
        return DeleteInstanceRequest.newBuilder()
                .setProject(vmAddress.project())
                .setZone(vmAddress.zone())
                .setInstance(vmAddress.vmName())
                .build();
    }

    private InsertInstanceRequest createInsertInstanceRequest(
            VMAddress vmAddress, Instance instance) {
        return InsertInstanceRequest.newBuilder()
                .setProject(vmAddress.project())
                .setZone(vmAddress.zone())
                .setInstanceResource(instance)
                .build();
    }
}
