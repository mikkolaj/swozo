package com.swozo.api.orchestrator;

import com.swozo.api.web.servicemodule.dto.ServiceConfigDto;
import com.swozo.exceptions.ServiceUnavailableException;
import com.swozo.mapper.ServiceModuleMapper;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrchestratorService {
    private final OrchestratorRequestSender requestSender;
    private final ServiceModuleMapper serviceModuleMapper;

    public OrchestratorLinkResponse getActivityLinks(Long scheduleRequestId) throws ServiceUnavailableException {
        return requestSender.getActivityLinks(scheduleRequestId).join();
    }

    public ScheduleResponse sendScheduleRequest(ScheduleRequest scheduleRequest) {
        return requestSender.sendScheduleRequest(scheduleRequest).join();
    }

    public Collection<ScheduleResponse> sendScheduleRequests(Collection<ScheduleRequest> scheduleRequests) {
        return requestSender.sendScheduleRequests(scheduleRequests).join();
    }

    public List<ServiceConfigDto> getSupportedServices() {
        return requestSender.getServiceConfigs().join().stream()
                .map(serviceModuleMapper::toDto)
                .toList();
    }
}
