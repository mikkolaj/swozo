package com.swozo.orchestrator.cloud.resources.vm;

import com.swozo.model.Psm;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface VMProvider {
    CompletableFuture<VMConnectionDetails> createInstance(Psm psm) throws InterruptedException, VMOperationFailed;

    Future<VMDeleted> deleteInstance(int internalResourceId) throws InterruptedException, VMOperationFailed;

    int getVMCreationSeconds(Psm psm);
}
