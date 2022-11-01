package com.swozo.orchestrator.cloud.resources.vm;


import com.swozo.model.scheduling.properties.Psm;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface TimedVMProvider {
    CompletableFuture<VMResourceDetails> createInstance(Psm psm, String namePrefix) throws InterruptedException, VMOperationFailed;

    CompletableFuture<Optional<VMResourceDetails>> getVMResourceDetails(Long internalId) throws VMOperationFailed;

    CompletableFuture<Void> deleteInstance(long internalResourceId) throws InterruptedException, VMOperationFailed;

    int getVMCreationTime(Psm psm);
}
