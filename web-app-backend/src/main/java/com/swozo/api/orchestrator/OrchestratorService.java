package com.swozo.api.orchestrator;

import com.swozo.api.orchestrator.exceptions.ServiceUnavailableException;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class OrchestratorService {
    private final OrchestratorRequestSender requestSender;

    public OrchestratorLinkResponse getActivityLinks(Long activityModuleID) throws ServiceUnavailableException {
        return requestSender.getActivityLinks(activityModuleID).join();
    }

    public void sendScheduleRequest(ScheduleRequest scheduleRequest) {
        requestSender.sendScheduleRequest(scheduleRequest).join();
    }

    public void sendScheduleRequest(Collection<ScheduleRequest> schedules) {
        // TODO in one request or all async
        schedules.forEach(this::sendScheduleRequest);
    }

}
