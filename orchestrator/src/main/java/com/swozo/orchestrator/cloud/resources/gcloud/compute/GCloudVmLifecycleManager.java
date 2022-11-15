package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.google.api.gax.rpc.NotFoundException;
import com.google.cloud.compute.v1.DeleteInstanceRequest;
import com.google.cloud.compute.v1.InsertInstanceRequest;
import com.google.cloud.compute.v1.Instance;
import com.google.cloud.compute.v1.InstancesClient;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.instance.InstanceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.networking.NetworkInterfaceProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.providers.storage.DiskProvider;
import com.swozo.orchestrator.cloud.resources.gcloud.configuration.GCloudProperties;
import com.swozo.orchestrator.cloud.resources.vm.VmOperationFailed;
import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Conditional(GCloudCondition.class)
public class GCloudVmLifecycleManager {
    private static final String DEFAULT_DISK_NAME = "disk-1";
    private static final int DEFAULT_NIC_INDEX = 0;
    private static final int DEFAULT_ACCESS_CONFIG_INDEX = 0;

    private final DiskProvider diskProvider;
    private final NetworkInterfaceProvider networkInterfaceProvider;
    private final InstanceProvider instanceProvider;
    private final GCloudProperties properties;
    private final Logger logger = LoggerFactory.getLogger(GCloudVmLifecycleManager.class);

    public CompletableFuture<Void> createInstance(VmAddress vmAddress, VMSpecs vmSpecs) {
        return CompletableFuture.runAsync(() -> {
            try (InstancesClient instancesClient = InstancesClient.create()) {
                var insertInstanceRequest = createInsertInstanceRequest(vmAddress, vmSpecs);

                logger.info("Creating instance: {} with specs {}", vmAddress, vmSpecs);

                instancesClient
                        .insertAsync(insertInstanceRequest)
                        .get(properties.computeRequestTimeoutMinutes(), TimeUnit.MINUTES);
            } catch (ExecutionException ex) {
                throw new VmOperationFailed(ex.getCause());
            } catch (Exception ex) {
                handleOtherExceptions(ex);
            }
        });
    }

    public CompletableFuture<Void> deleteInstance(VmAddress vmAddress) {
        return CompletableFuture.runAsync(() -> {
            try (var instancesClient = InstancesClient.create()) {
                var deleteInstanceRequest = createDeleteInstanceRequest(vmAddress);

                logger.info("Deleting instance: {}", vmAddress.vmName());

                instancesClient
                        .deleteAsync(deleteInstanceRequest)
                        .get(properties.computeRequestTimeoutMinutes(), TimeUnit.MINUTES);
            } catch (ExecutionException ex) {
                handleMissingResource(vmAddress, ex);
            } catch (Exception ex) {
                handleOtherExceptions(ex);
            }
        });
    }

    private static void handleOtherExceptions(Exception ex) {
        CheckedExceptionConverter.from(() -> {
            throw ex;
        }, VmOperationFailed::new).get();
    }

    private void handleMissingResource(VmAddress vmAddress, ExecutionException ex) {
        if (resourceDoesntExist(ex)) {
            logger.warn("Instance: {} has already been deleted.", vmAddress);
        } else {
            throw new VmOperationFailed(ex.getCause());
        }
    }

    private boolean resourceDoesntExist(ExecutionException ex) {
        return ex.getCause() instanceof NotFoundException;
    }

    public String getInstanceExternalIP(VmAddress vmAddress) throws VmOperationFailed {
        try (var instancesClient = InstancesClient.create()) {
            return instancesClient.get(vmAddress.project(), vmAddress.zone(), vmAddress.vmName())
                    .getNetworkInterfaces(DEFAULT_NIC_INDEX)
                    .getAccessConfigs(DEFAULT_ACCESS_CONFIG_INDEX)
                    .getNatIP();
        } catch (IOException e) {
            throw new VmOperationFailed(e);
        }
    }

    private DeleteInstanceRequest createDeleteInstanceRequest(VmAddress vmAddress) {
        return DeleteInstanceRequest.newBuilder()
                .setProject(vmAddress.project())
                .setZone(vmAddress.zone())
                .setInstance(vmAddress.vmName())
                .build();
    }

    private InsertInstanceRequest createInsertInstanceRequest(VmAddress vmAddress, VMSpecs vmSpecs) {
        var instance = createInstanceRepresentation(vmAddress, vmSpecs);

        return InsertInstanceRequest.newBuilder()
                .setProject(vmAddress.project())
                .setZone(vmAddress.zone())
                .setInstanceResource(instance)
                .build();
    }

    private Instance createInstanceRepresentation(VmAddress vmAddress, VMSpecs vmSpecs) {
        var disk = diskProvider.createDisk(DEFAULT_DISK_NAME, vmSpecs.imageFamily(), vmSpecs.diskSizeGB());

        var networkInterface = networkInterfaceProvider.createNetworkInterface(vmAddress.networkName());

        return instanceProvider.createInstance(vmAddress, vmSpecs, disk, networkInterface);
    }
}
