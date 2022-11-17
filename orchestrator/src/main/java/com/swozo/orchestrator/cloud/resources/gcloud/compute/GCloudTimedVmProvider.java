package com.swozo.orchestrator.cloud.resources.gcloud.compute;

import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpStatusCodes;
import com.google.api.gax.rpc.AbortedException;
import com.swozo.model.scheduling.properties.MdaVmSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMSpecs;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmAddress;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VmStatus;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.TransactionalVmUtils;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.VmEntity;
import com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence.VmRepository;
import com.swozo.orchestrator.cloud.resources.gcloud.configuration.GCloudProperties;
import com.swozo.orchestrator.cloud.resources.vm.TimedVmProvider;
import com.swozo.orchestrator.cloud.resources.vm.VmOperationFailed;
import com.swozo.orchestrator.cloud.resources.vm.VmResourceDetails;
import com.swozo.orchestrator.configuration.conditions.GCloudCondition;
import com.swozo.utils.RetryHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.PrimitiveIterator;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static com.swozo.utils.LoggingUtils.logIfSuccess;

@Service
@RequiredArgsConstructor
@Conditional(GCloudCondition.class)
public class GCloudTimedVmProvider implements TimedVmProvider {
    private static final int VM_CREATION_SECONDS = 5 * 60;
    private static final int DEFAULT_SSH_PORT = 22;
    private static final String DEFAULT_NETWORK = "default";

    private final GCloudProperties gCloudProperties;
    private final GCloudVmLifecycleManager manager;
    private final VmRepository vmRepository;
    private final TransactionalVmUtils vmUtils;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Async
    @Override
    @Transactional
    public CompletableFuture<VmResourceDetails> createInstance(MdaVmSpecs mdaVmSpecs, String namePrefix) {
        return handleVmNameCollisions(mdaVmSpecs, namePrefix)
                .thenCompose(this::getVmData)
                .thenCompose(this::getWrappedVmResourceDetails)
                .whenComplete(logIfSuccess(logger, "Successfully created resource: {}"));
    }

    private CompletableFuture<VmAddress> handleVmNameCollisions(MdaVmSpecs mdaVmSpecs, String namePrefix) {
        var supplier = IntStream.iterate(0, i -> i + 1).iterator();

        return RetryHandler.withImmediateRetries(
                () -> tryCreatingVmWithName(mdaVmSpecs, appendSuffix(namePrefix, supplier)),
                3
        );
    }

    private String appendSuffix(String namePrefix, PrimitiveIterator.OfInt supplier) {
        return String.format("%s-%d", namePrefix, supplier.nextInt());
    }

    private CompletableFuture<VmAddress> tryCreatingVmWithName(MdaVmSpecs mdaVmSpecs, String name) {
        var vmAddress = getVMAddress(name);
        var vmSpecs = getVMSpecs(mdaVmSpecs);
        return manager.createInstance(vmAddress, vmSpecs)
                .whenComplete((msg, ex) -> logConflicts(name, ex))
                .thenCompose(n -> CompletableFuture.completedFuture(vmAddress));
    }

    private void logConflicts(String name, Throwable ex) {
        if (ex != null && isCausedByConflict(ex)) {
            logger.warn("Name conflict while trying to create instance: {}", name);
        }
    }

    private boolean isCausedByConflict(Throwable ex) {
        return ex.getCause() instanceof AbortedException rpcCause
                && rpcCause.getCause() instanceof HttpResponseException httpCause
                && httpCause.getStatusCode() == HttpStatusCodes.STATUS_CODE_CONFLICT;
    }

    private CompletableFuture<VmEntityWithAddress> getVmData(VmAddress vmAddress) {
        var vmEntity = vmUtils.save(vmAddress);
        var publicIPAddress = manager.getInstanceExternalIP(vmAddress);
        return CompletableFuture.completedFuture(new VmEntityWithAddress(vmEntity, publicIPAddress));
    }

    private CompletableFuture<VmResourceDetails> getWrappedVmResourceDetails(VmEntityWithAddress vmData) {
        return CompletableFuture.completedFuture(getVMResourceDetails(vmData.publicIp, vmData.vm.getId()));
    }

    @Async
    @Override
    public CompletableFuture<VmResourceDetails> getVMResourceDetails(long internalId) {
        return CompletableFuture.completedFuture(vmRepository.findById(internalId)
                .map(vmUtils::toDto)
                .map(manager::getInstanceExternalIP)
                .map(address ->
                        new VmResourceDetails(internalId, address, gCloudProperties.sshUser(), DEFAULT_SSH_PORT, gCloudProperties.sshKeyPath()))
                .orElseThrow(() -> new VmOperationFailed(String.format("No Vm with id: %s", internalId))));
    }

    @Async
    @Override
    public CompletableFuture<Void> deleteInstance(long internalId) {
        return CompletableFuture.completedFuture(vmRepository
                        .findById(internalId)
                        .orElseThrow(() -> new VmOperationFailed(String.format("No instance with id: %s", internalId)))
                )
                .thenCompose(vmEntity -> manager.deleteInstance(vmUtils.toDto(vmEntity))
                        .thenRun(() -> vmUtils.updateStatus(vmEntity, VmStatus.DELETED))
                        .thenRun(() -> logger.info("Successfully deleted vm: {}", vmEntity))
                );
    }

    @Override
    public int getVMCreationTime(MdaVmSpecs mdaVmSpecs) {
        // TODO: creation time based on machine type
        return VM_CREATION_SECONDS;
    }


    private VmResourceDetails getVMResourceDetails(String publicIPAddress, Long internalId) {
        return new VmResourceDetails(internalId, publicIPAddress, gCloudProperties.sshUser(), DEFAULT_SSH_PORT, gCloudProperties.sshKeyPath());
    }

    private VmAddress getVMAddress(String name) {
        return new VmAddress(gCloudProperties.project(), gCloudProperties.zone(), DEFAULT_NETWORK, name);
    }

    private VMSpecs getVMSpecs(MdaVmSpecs mdaVmSpecs) {
        return new VMSpecs(mdaVmSpecs.machineType(), gCloudProperties.computeImageFamily(), mdaVmSpecs.diskSizeGb());
    }

    private record VmEntityWithAddress(VmEntity vm, String publicIp) {
    }
}
