package com.swozo.api.orchestratorclient;

import com.swozo.api.exceptions.ServiceUnavailableException;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrchestratorService {
    private final OrchestratorRequestSender requestSender;

    @Autowired
    OrchestratorService(OrchestratorRequestSender requestSender) {
        this.requestSender = requestSender;
    }

    public OrchestratorLinkResponse getActivityLinks(Long activityModuleID) throws ServiceUnavailableException {
        return requestSender.getActivityLinks(activityModuleID);
    }

    public void postScheduleRequest(ScheduleRequest scheduleRequest) throws IllegalArgumentException {
        requestSender.postScheduleRequest(scheduleRequest);
    }

}
