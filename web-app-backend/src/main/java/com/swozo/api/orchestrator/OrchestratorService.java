package com.swozo.api.orchestrator;

import com.swozo.api.orchestrator.exceptions.ServiceUnavailableException;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class OrchestratorService {
    private final OrchestratorRequestSender requestSender;
    private final JsonMapperFacade mapper;

    public OrchestratorLinkResponse getActivityLinks(Long scheduleRequestId) throws ServiceUnavailableException {
        return requestSender.getActivityLinks(scheduleRequestId).join();
    }

    public ScheduleResponse sendScheduleRequest(ScheduleRequest scheduleRequest) {
        return requestSender.sendScheduleRequest(scheduleRequest).join();
    }

    public Collection<ScheduleResponse> sendScheduleRequests(Collection<ScheduleRequest> scheduleRequests) {
        return requestSender.sendScheduleRequests(scheduleRequests).join();
    }

}
