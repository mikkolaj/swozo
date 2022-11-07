package com.swozo.api.orchestrator;

import com.swozo.model.scheduling.ServiceConfig;

import java.util.List;
import java.util.function.Supplier;

public interface OrchestratorCache {
    List<ServiceConfig> getServiceConfigs(Supplier<List<ServiceConfig>> cacheMissSupplier);
    ServiceConfig getServiceConfig(Supplier<List<ServiceConfig>> cacheMissSupplier, String serviceName);
}
