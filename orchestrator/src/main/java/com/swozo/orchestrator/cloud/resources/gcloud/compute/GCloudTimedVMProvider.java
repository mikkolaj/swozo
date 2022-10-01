package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.swozo.model.scheduling.properties.Psm;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.configuration.GCloudProperties;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
@RequiredArgsConstructor
@Conditional(GCloudCondition.class)
public class GCloudTimedVMProvider implements TimedVMProvider {
    private static final int VM_CREATION_SECONDS = 5 * 60;
    private static final int DEFAULT_SSH_PORT = 22;
    private static final String DEFAULT_NETWORK = "default";

    private final GCloudProperties gCloudProperties;
    private final GCloudVMLifecycleManager manager;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    // TODO: proper resource persistence
    private final List<VMAddress> resources = new ArrayList<>();

    @Async
    @Override
    public CompletableFuture<VMResourceDetails> createInstance(Psm psm) throws InterruptedException, VMOperationFailed {
        try {
            // TODO: create unique name
            var vmAddress = getVMAddress("super-instancja");
            var vmSpecs = getVMSpecs(psm);
            manager.createInstance(vmAddress, vmSpecs);
            var publicIPAddress = manager.getInstanceExternalIP(vmAddress);
            var internalId = resources.size();
            resources.add(vmAddress);
            return CompletableFuture
                    .completedFuture(getVMResourceDetails(publicIPAddress, internalId))
                    .whenComplete((resourceDetails, error) -> logger.info("Successfully created resource: {}", resourceDetails));
        } catch (IOException | ExecutionException | TimeoutException e) {
            throw new VMOperationFailed(e);
        }
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteInstance(int internalId) throws InterruptedException, VMOperationFailed {
        if (internalId > resources.size() || resources.get(internalId) == null) {
            throw new VMOperationFailed("No such instance");
        }
        try {
            var vmAddress = resources.get(internalId);
            manager.deleteInstance(vmAddress);
            return CompletableFuture
                    .completedFuture(null)
                    .thenRun(() -> logger.info("Successfully deleted vm: {}", vmAddress));
        } catch (IOException | ExecutionException | TimeoutException e) {
            throw new VMOperationFailed(e);
        }
    }

    @Override
    public int getVMCreationTime(Psm psm) {
        // TODO: creation time based on machine type
        return VM_CREATION_SECONDS;
    }

    private VMResourceDetails getVMResourceDetails(String publicIPAddress, int internalId) {
        return new VMResourceDetails(internalId, publicIPAddress, gCloudProperties.sshUser(), DEFAULT_SSH_PORT, gCloudProperties.sshKeyPath());
    }

    private VMAddress getVMAddress(String name) {
        return new VMAddress(gCloudProperties.project(), gCloudProperties.zone(), DEFAULT_NETWORK, name);
    }

    private VMSpecs getVMSpecs(Psm psm) {
        return new VMSpecs(psm.machineType(), gCloudProperties.computeImageFamily(), psm.diskSizeGb());
    }
}
