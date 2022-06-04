package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.google.cloud.compute.v1.*;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DiskProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMLifecycleManager;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


@Service
@RequiredArgsConstructor
public class GCloudVMLifecycleManager implements VMLifecycleManager {
    private static final String DEFAULT_DISK_NAME = "disk-1";

    private static final int GCLOUD_TIMEOUT_MINUTES = 3;

    private final DiskProvider diskProvider;
    private final NetworkInterfaceProvider networkInterfaceProvider;
    private final InstanceProvider instanceProvider;
    private final Logger logger = LoggerFactory.getLogger(GCloudVMLifecycleManager.class);

    @Async
    public Future<Operation> createInstance(VMAddress vmAddress, VMSpecs vmSpecs)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (InstancesClient instancesClient = InstancesClient.create()) {
            var disk = diskProvider.createDisk(DEFAULT_DISK_NAME, vmSpecs.imageFamily(), vmSpecs.diskSizeGB());

            var networkInterface = networkInterfaceProvider.createNetworkInterface(vmAddress.networkName());

            var instanceResource = instanceProvider.createInstance(vmAddress, vmSpecs, disk, networkInterface);

            var insertInstanceRequest = createInsertInstanceRequest(vmAddress, instanceResource);

            logger.info(String.format("Creating instance: %s at %s %n", vmAddress.vmName(), vmAddress.zone()));

            var futureOperation = instancesClient.insertAsync(insertInstanceRequest);

            return new AsyncResult<>(futureOperation.get(GCLOUD_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        }
    }

    @Async
    public Future<Operation> deleteInstance(VMAddress vmAddress)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (var instancesClient = InstancesClient.create()) {
            var deleteInstanceRequest = createDeleteInstanceRequest(vmAddress);

            logger.info(String.format("Deleting instance: %s ", vmAddress.vmName()));

            var futureOperation = instancesClient.deleteAsync(deleteInstanceRequest);

            return new AsyncResult<>(futureOperation.get(GCLOUD_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        }
    }

    private DeleteInstanceRequest createDeleteInstanceRequest(VMAddress vmAddress) {
        return DeleteInstanceRequest.newBuilder()
                .setProject(vmAddress.project())
                .setZone(vmAddress.zone())
                .setInstance(vmAddress.vmName())
                .build();
    }


    private InsertInstanceRequest createInsertInstanceRequest(VMAddress vmAddress, Instance instance) {
        return InsertInstanceRequest.newBuilder()
                .setProject(vmAddress.project())
                .setZone(vmAddress.zone())
                .setInstanceResource(instance)
                .build();
    }


}
