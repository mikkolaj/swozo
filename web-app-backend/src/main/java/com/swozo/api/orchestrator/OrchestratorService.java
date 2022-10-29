package com.swozo.api.orchestrator;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrchestratorService {
    private final OrchestratorRequestSender requestSender;

    public ScheduleResponse sendScheduleRequest(ScheduleRequest scheduleRequest) {
        return requestSender.sendScheduleRequest(scheduleRequest).join();
    }

    public Collection<ScheduleResponse> sendScheduleRequests(Collection<ScheduleRequest> scheduleRequests) {
        return requestSender.sendScheduleRequests(scheduleRequests).join();
    }

    // TODO: this should be cached
    public List<ServiceConfig> getSupportedServices() {
        return requestSender.getServiceConfigs().join();
    }

    public ServiceConfig getServiceConfig(String scheduleType) {
        return requestSender.getServiceConfig(scheduleType).join();
    }
}
