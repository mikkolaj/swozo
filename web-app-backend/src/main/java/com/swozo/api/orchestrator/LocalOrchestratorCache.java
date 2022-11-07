package com.swozo.api.orchestrator;

import com.swozo.config.EnvNames;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.util.LocalAggregateReadonlyCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

@Component
public class LocalOrchestratorCache implements OrchestratorCache {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final LocalAggregateReadonlyCache<ServiceConfig> cache;

    public LocalOrchestratorCache(
            @Value("${" + EnvNames.SERVICE_CONFIG_CACHE_REVALIDATE_DURATION + "}") Duration serviceCacheRevalidateAfter
    ) {
        this.cache = new LocalAggregateReadonlyCache<>(
                serviceCacheRevalidateAfter,
                ServiceConfig::serviceName,
                () -> logger.info("Revalidating service config cache")
        );
    }

    @Override
    public List<ServiceConfig> getServiceConfigs(Supplier<List<ServiceConfig>> cacheMissSupplier) {
        return cache.getAll(cacheMissSupplier);
    }

    @Override
    public ServiceConfig getServiceConfig(Supplier<List<ServiceConfig>> cacheMissSupplier, String serviceName) {
        return cache.get(serviceName, cacheMissSupplier)
                .orElseThrow(() -> {
                    logger.error("Service config for service: {} not found", serviceName);
                    throw new IllegalArgumentException("No service named " + serviceName);
                });
    }
}
