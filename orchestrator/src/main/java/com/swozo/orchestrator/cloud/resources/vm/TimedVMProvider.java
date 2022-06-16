package com.swozo.orchestrator.cloud.resources.vm;


import com.swozo.model.scheduling.properties.Psm;

import java.util.concurrent.CompletableFuture;

public interface TimedVMProvider {
    CompletableFuture<VMResourceDetails> createInstance(Psm psm) throws InterruptedException, VMOperationFailed;

    CompletableFuture<Void> deleteInstance(int internalResourceId) throws InterruptedException, VMOperationFailed;

    int getVMCreationTime(Psm psm);
}
