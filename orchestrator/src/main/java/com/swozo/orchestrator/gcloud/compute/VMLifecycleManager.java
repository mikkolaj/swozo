package com.swozo.orchestrator.gcloud.compute;

import com.google.api.gax.longrunning.OperationFuture;
import com.google.cloud.compute.v1.*;
import com.swozo.orchestrator.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.gcloud.compute.providers.storage.DiskProvider;
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
public class VMLifecycleManager {
    private static final String defaultDiskName = "disk-1";

    private static final int GCLOUD_TIMEOUT_MINUTES = 3;

    private final DiskProvider diskProvider;
    private final NetworkInterfaceProvider networkInterfaceProvider;
    private final InstanceProvider instanceProvider;
    private final Logger logger = LoggerFactory.getLogger(VMLifecycleManager.class);

    @Async
    public Future<Operation> createInstance(VMAddress vmAddress, VMSpecs vmSpecs)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (InstancesClient instancesClient = InstancesClient.create()) {
            var disk = diskProvider.createDisk(defaultDiskName, vmSpecs.imageFamily(), vmSpecs.diskSizeGB());

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
            var deleteInstanceRequest = createDeleteInstanceRequest(vmAddress.project(), vmAddress.zone(), vmAddress.vmName());

            logger.info(String.format("Deleting instance: %s ", vmAddress.vmName()));

            var futureOperation = instancesClient.deleteAsync(deleteInstanceRequest);

            return new AsyncResult<>(futureOperation.get(GCLOUD_TIMEOUT_MINUTES, TimeUnit.MINUTES));
        }
    }

    private DeleteInstanceRequest createDeleteInstanceRequest(String project, String zone, String vmName) {
        return DeleteInstanceRequest.newBuilder()
                .setProject(project)
                .setZone(zone)
                .setInstance(vmName)
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
