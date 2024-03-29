package com.swozo.api.orchestrator;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
public class OrchestratorService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final OrchestratorRequestSender requestSender;
    private final OrchestratorCache cache;

    public ScheduleResponse sendScheduleRequest(ScheduleRequest scheduleRequest) {
        return requestSender.sendScheduleRequest(scheduleRequest).join();
    }

    public Collection<ScheduleResponse> sendScheduleRequests(Collection<ScheduleRequest> scheduleRequests) {
        logger.debug("Sending schedule requests: {}", scheduleRequests);
        return requestSender.sendScheduleRequests(scheduleRequests).join();
    }

    public List<ServiceConfig> getSupportedServices() {
        return cache.getServiceConfigs(serviceConfigsSupplier());
    }

    public ServiceConfig getServiceConfig(String serviceName) {
        return cache.getServiceConfig(serviceConfigsSupplier(), serviceName);
    }

    public void cancelScheduleRequests(Collection<Long> scheduleRequestIds) {
        logger.info("Cancelling schedule requests: {}", scheduleRequestIds);
        CompletableFuture.allOf(
                scheduleRequestIds.stream()
                    .map(requestSender::cancelScheduleRequest)
                    .toArray(CompletableFuture[]::new)
                )
                .join();
        logger.debug("Requests: {} cancelled successfully", scheduleRequestIds);
    }

    public LocalDateTime getEstimatedAsapServiceAvailability(String serviceName) {
        return requestSender.getEstimatedAsapServiceAvailability(serviceName).join();
    }

    private Supplier<List<ServiceConfig>> serviceConfigsSupplier() {
        return () -> requestSender.getServiceConfigs().join();
    }
}
