package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.gax.rpc.AbortedException;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMStatus;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.VMEntity;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.VMMapper;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.VMRepository;
import com.swozo.orchestrator.cloud.resources.gcloud.configuration.GCloudProperties;
import com.swozo.orchestrator.cloud.resources.vm.TimedVMProvider;
import com.swozo.orchestrator.cloud.resources.vm.VMOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VMResourceDetails;
import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import com.swozo.utils.CheckedExceptionConverter;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Optional;
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
    private final VMRepository vmRepository;
    private final VMMapper vmMapper;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    @Override
    @Transactional
    public CompletableFuture<VMResourceDetails> createInstance(Psm psm, String namePrefix) throws InterruptedException, VMOperationFailed {
        try {
            var vmAddress = handleVmNameCollisions(psm, namePrefix);
            var vmEntity = vmRepository.save(vmMapper.toPersistence(vmAddress));
            updateStatus(vmEntity, VMStatus.CREATED);
            var publicIPAddress = manager.getInstanceExternalIP(vmAddress);
            return CompletableFuture
                    .completedFuture(getVMResourceDetails(publicIPAddress, vmEntity.getId()))
                    .whenComplete((resourceDetails, error) -> logger.info("Successfully created resource: {}", resourceDetails));
        } catch (IOException | ExecutionException | TimeoutException e) {
            throw new VMOperationFailed(e);
        }
    }

    private VMAddress handleVmNameCollisions(Psm psm, String namePrefix) throws IOException, ExecutionException, TimeoutException, InterruptedException {
        var suffix = 0;
        Optional<VMAddress> vmAddress = Optional.empty();

        while (vmAddress.isEmpty()) {
            var nameToTry = String.format("%s-%d", namePrefix, suffix);
            vmAddress = tryCreatingVmWithName(psm, String.format(nameToTry));
            suffix += 1;
        }
        return vmAddress.get();
    }

    private Optional<VMAddress> tryCreatingVmWithName(Psm psm, String name) throws IOException, ExecutionException, TimeoutException, InterruptedException {
        try {
            var vmAddress = getVMAddress(name);
            var vmSpecs = getVMSpecs(psm);
            manager.createInstance(vmAddress, vmSpecs);
            return Optional.of(vmAddress);
        } catch (ExecutionException ex) {
            if (isCausedByConflict(ex)) {
                logger.warn("Name conflict while trying to create instance: {}", name);
            } else {
                throw ex;
            }
        }
        return Optional.empty();
    }

    private boolean isCausedByConflict(ExecutionException ex) {
        return ex.getCause() instanceof AbortedException rpcCause
                && rpcCause.getCause() instanceof HttpResponseException httpCause
                && httpCause.getStatusCode() == HttpStatusCodes.STATUS_CODE_CONFLICT;
    }

    @Async
    @Override
    public CompletableFuture<Optional<VMResourceDetails>> getVMResourceDetails(Long internalId) throws VMOperationFailed {
        return CompletableFuture.completedFuture(vmRepository.findById(internalId)
                .map(vmMapper::toDto)
                .map(CheckedExceptionConverter.from(manager::getInstanceExternalIP, VMOperationFailed::new))
                .map(address ->
                        new VMResourceDetails(internalId, address, gCloudProperties.sshUser(), DEFAULT_SSH_PORT, gCloudProperties.sshKeyPath()))
        );
    }

    @Async
    @Override
    @Transactional
    public CompletableFuture<Void> deleteInstance(long internalId) throws InterruptedException, VMOperationFailed {
        try {
            var vmEntity = vmRepository
                    .findById(internalId)
                    .orElseThrow(() -> new VMOperationFailed(String.format("No instance with id: %s", internalId)));
            manager.deleteInstance(vmMapper.toDto(vmEntity));
            updateStatus(vmEntity, VMStatus.DELETED);
            return CompletableFuture
                    .completedFuture(null)
                    .thenRun(() -> logger.info("Successfully deleted vm: {}", vmEntity));
        } catch (IOException | ExecutionException | TimeoutException e) {
            throw new VMOperationFailed(e);
        }
    }

    @Override
    public int getVMCreationTime(Psm psm) {
        // TODO: creation time based on machine type
        return VM_CREATION_SECONDS;
    }


    private VMResourceDetails getVMResourceDetails(String publicIPAddress, Long internalId) {
        return new VMResourceDetails(internalId, publicIPAddress, gCloudProperties.sshUser(), DEFAULT_SSH_PORT, gCloudProperties.sshKeyPath());
    }

    private VMAddress getVMAddress(String name) {
        return new VMAddress(gCloudProperties.project(), gCloudProperties.zone(), DEFAULT_NETWORK, name);
    }

    private VMSpecs getVMSpecs(Psm psm) {
        return new VMSpecs(psm.machineType(), gCloudProperties.computeImageFamily(), psm.diskSizeGb());
    }

    private void updateStatus(VMEntity entity, VMStatus status) {
        entity.setStatus(status);
        vmRepository.save(entity);
    }
}
