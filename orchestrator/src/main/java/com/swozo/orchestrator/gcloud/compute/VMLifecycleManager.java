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
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
public class VMLifecycleManager {
    private static final String defaultDiskName = "disk-1";

    private final DiskProvider diskProvider;
    private final NetworkInterfaceProvider networkInterfaceProvider;
    private final InstanceProvider instanceProvider;
    private final Logger logger = LoggerFactory.getLogger(VMLifecycleManager.class);

    public OperationFuture<Operation, Operation> createInstance(VMAddress vmAddress, VMSpecs vmSpecs)
            throws IOException {

        try (InstancesClient instancesClient = InstancesClient.create()) {
            var disk = diskProvider.createDisk(defaultDiskName, vmSpecs.imageFamily(), vmSpecs.diskSizeGB());

            var networkInterface = networkInterfaceProvider.createNetworkInterface(vmAddress.networkName());

            var instanceResource = instanceProvider.createInstance(vmAddress, vmSpecs, disk, networkInterface);

            var insertInstanceRequest = createInsertInstanceRequest(vmAddress, instanceResource);

            logger.info(String.format("Creating instance: %s at %s %n", vmAddress.vmName(), vmAddress.zone()));

            return instancesClient.insertAsync(insertInstanceRequest);
        }
    }

    public OperationFuture<Operation, Operation> deleteInstance(VMAddress vmAddress)
            throws IOException {
        try (var instancesClient = InstancesClient.create()) {
            var deleteInstanceRequest = createDeleteInstanceRequest(vmAddress.project(), vmAddress.zone(), vmAddress.vmName());

            logger.info(String.format("Deleting instance: %s ", vmAddress.vmName()));

            return instancesClient.deleteAsync(deleteInstanceRequest);
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

    @PostConstruct
    // testując tutaj rzuca thread pool rzuca exception, ale request sie wykonuje
    public void init() {
        var project = "hybrid-text-350213";
        var zone = "us-west2-a";
        var networkName = "default";
        var vmName = "super-instancja";

        var vmAddress = new VMAddress(project, zone, networkName, vmName);
        var vmSpecs = new VMSpecs("e2-micro", "debian-11", 10);

//        try {
//            var operation = createInstance(vmAddress, vmSpecs);
//            var response = operation.get(3, TimeUnit.MINUTES);
//
//            if (response.hasError()) {
//                logger.error("Instance creation failed! " + response);
//            } else {
//                logger.info("Operation Status: " + response.getStatus());
//            }
//        } catch (Exception exception) {
//            logger.error("Failed to create instance. " + vmAddress + " " + exception);
//        }

        try {
            var operation = deleteInstance(vmAddress);
            var response = operation.get(3, TimeUnit.MINUTES);

            if (response.hasError()) {
                logger.error("Instance creation failed! " + response);
            } else {
                logger.info("Operation Status: " + response.getStatus());
            }
        } catch (Exception exception) {
            logger.error("Failed to delete instance. " + vmAddress + " " + exception);
        }
    }
}
