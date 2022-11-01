package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteInstanceRequest;
import com.google.cloud.compute.v1.InsertInstanceRequest;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DiskProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.configuration.GCloudProperties;
import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Conditional(GCloudCondition.class)
public class GCloudVMLifecycleManager {
    private static final String DEFAULT_DISK_NAME = "disk-1";
    private static final int DEFAULT_NIC_INDEX = 0;
    private static final int DEFAULT_ACCESS_CONFIG_INDEX = 0;

    private final DiskProvider diskProvider;
    private final NetworkInterfaceProvider networkInterfaceProvider;
    private final InstanceProvider instanceProvider;
    private final GCloudProperties properties;
    private final Logger logger = LoggerFactory.getLogger(GCloudVMLifecycleManager.class);

    public void createInstance(VMAddress vmAddress, VMSpecs vmSpecs)
            throws IOException, ExecutionException, TimeoutException, InterruptedException {
        try (InstancesClient instancesClient = InstancesClient.create()) {
            var insertInstanceRequest = createInsertInstanceRequest(vmAddress, vmSpecs);

            logger.info("Creating instance: {} with specs {}", vmAddress, vmSpecs);

            instancesClient
                    .insertAsync(insertInstanceRequest)
                    .get(properties.computeRequestTimeoutMinutes(), TimeUnit.MINUTES);
        }
    }

    public void deleteInstance(VMAddress vmAddress)
            throws IOException, ExecutionException, InterruptedException, TimeoutException {
        try (var instancesClient = InstancesClient.create()) {
            var deleteInstanceRequest = createDeleteInstanceRequest(vmAddress);

            logger.info("Deleting instance: {}", vmAddress.vmName());

            instancesClient
                    .deleteAsync(deleteInstanceRequest)
                    .get(properties.computeRequestTimeoutMinutes(), TimeUnit.MINUTES);
        } catch (ExecutionException ex) {
            if (resourceDoesntExist(ex)) {
                logger.warn("Instance: {} has already been deleted.", vmAddress);
            } else {
                throw ex;
            }
        }
    }

    private boolean resourceDoesntExist(ExecutionException ex) {
        return ex.getCause() instanceof NotFoundException;
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

    private InsertInstanceRequest createInsertInstanceRequest(VMAddress vmAddress, VMSpecs vmSpecs) {
        var instance = createInstanceRepresentation(vmAddress, vmSpecs);

        return InsertInstanceRequest.newBuilder()
                .setProject(vmAddress.project())
                .setZone(vmAddress.zone())
                .setInstanceResource(instance)
                .build();
    }

    private Instance createInstanceRepresentation(VMAddress vmAddress, VMSpecs vmSpecs) {
        var disk = diskProvider.createDisk(DEFAULT_DISK_NAME, vmSpecs.imageFamily(), vmSpecs.diskSizeGB());

        var networkInterface = networkInterfaceProvider.createNetworkInterface(vmAddress.networkName());

        return instanceProvider.createInstance(vmAddress, vmSpecs, disk, networkInterface);
    }
}
