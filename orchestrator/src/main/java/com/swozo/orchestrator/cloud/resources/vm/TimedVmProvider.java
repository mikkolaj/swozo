package com.swozo.orchestrator.cloud.resources.vm;


import com.swozo.model.scheduling.properties.MdaVmSpecs;

import java.util.concurrent.CompletableFuture;

public interface TimedVmProvider {
    CompletableFuture<VmResourceDetails> createInstance(MdaVmSpecs mdaVmSpecs, String namePrefix);

    CompletableFuture<VmResourceDetails> getVMResourceDetails(Long internalId);

    CompletableFuture<Void> deleteInstance(long internalResourceId);

    int getVMCreationTime(MdaVmSpecs mdaVmSpecs);
}
