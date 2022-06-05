package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.swozo.model.Psm;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.vm.VMConnectionDetails;
import com.swozo.orchestrator.cloud.resources.vm.VMDeleted;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.configuration.EnvNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@Service
public class GCloudTimedVMProvider implements TimedVMProvider {
    private static final int VM_CREATION_SECONDS = 5 * 60;
    private static final String DEFAULT_NETWORK = "default";
    private final String project;
    private final String zone;
    private final String imageFamily;
    private final String sshUser;
    private final String sshKeyPath;
    private final GCloudVMLifecycleManager manager;
    private final Logger logger;
    // TODO: proper resource persistence
    private final List<VMAddress> resources;

    public GCloudTimedVMProvider(
            @Value("${" + EnvNames.GCP_PROJECT + "}") String project,
            @Value("${" + EnvNames.GCP_ZONE + "}") String zone,
            @Value("${" + EnvNames.GCP_VM_IMAGE_FAMILY + "}") String imageFamily,
            @Value("${" + EnvNames.GCP_SSH_USER + "}") String sshUser,
            @Value("${" + EnvNames.GCP_SSH_KEY_PATH + "}") String sshKeyPath,
            GCloudVMLifecycleManager manager) {
        this.project = project;
        this.zone = zone;
        this.imageFamily = imageFamily;
        this.manager = manager;
        this.sshUser = sshUser;
        this.sshKeyPath = sshKeyPath;
        this.logger = LoggerFactory.getLogger(this.getClass());
        this.resources = new ArrayList<>();
    }

    @Async
    @Override
    public CompletableFuture<VMConnectionDetails> createInstance(
            Psm psm) throws InterruptedException, VMOperationFailed {
        try {
            // TODO: create unique name
            var vmAddress = getVMAddress("uniqueName");
            var vmSpecs = getVMSpecs(psm);
            manager.createInstance(vmAddress, vmSpecs);
            var publicIPAddress = manager.getInstanceExternalIP(vmAddress);
            var internalId = resources.size();
            resources.add(vmAddress);
            return CompletableFuture.completedFuture(getVMConnectionDetails(publicIPAddress, internalId));
        } catch (IOException | ExecutionException | TimeoutException e) {
            logger.error(e.getMessage());
            var newException = new VMOperationFailed(e.getMessage());
            newException.setStackTrace(e.getStackTrace());
            throw newException;
        }
    }

    @Async
    @Override
    public CompletableFuture<VMDeleted> deleteInstance(int internalId) throws InterruptedException, VMOperationFailed {
        if (internalId > resources.size() || resources.get(internalId) == null) {
            throw new VMOperationFailed("No such instance");
        }
        try {
            var vmAddress = resources.get(internalId);
            manager.deleteInstance(vmAddress);
            return CompletableFuture.completedFuture(new VMDeleted());
        } catch (IOException | ExecutionException | TimeoutException e) {
            throw new VMOperationFailed(e.getMessage());
        }
    }

    @Override
    public int getVMCreationTime(Psm psm) {
        // TODO: creation time based on machine type
        return VM_CREATION_SECONDS;
    }

    private VMConnectionDetails getVMConnectionDetails(String publicIPAddress, int internalId) {
        return new VMConnectionDetails(internalId, publicIPAddress, sshUser, sshKeyPath);
    }

    private VMAddress getVMAddress(String name) {
        return new VMAddress(project, zone, DEFAULT_NETWORK, name);
    }

    private VMSpecs getVMSpecs(Psm psm) {
        return new VMSpecs(psm.machineType(), imageFamily, psm.discSizeGB());
    }
}
