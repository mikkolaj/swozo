package com.swozo.api.orchestratorclient;

import com.swozo.api.exceptions.ServiceType;
import com.swozo.api.exceptions.ServiceUnavailableException;
import com.swozo.jsonmapper.JsonMapper;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class OrchestratorService {
    private final OrchestratorRequestSender orchestratorController;

    OrchestratorService(OrchestratorRequestSender orchestratorController) {
        this.orchestratorController = orchestratorController;
    }

    public OrchestratorLinkResponse getActivityLinks(Long activityModuleID) throws IllegalArgumentException {
        String jsonLinks = orchestratorController.getActivityLinks(activityModuleID);

        Optional<OrchestratorLinkResponse> response = JsonMapper.mapJsonToLinkResponse(jsonLinks);

        return response
                .orElseThrow(() -> new ServiceUnavailableException(ServiceType.ORCHESTRATOR));
    }

    public boolean postScheduleRequest(ScheduleRequest scheduleRequest) throws IllegalArgumentException {
        Optional<String> mappedRequest = JsonMapper.mapScheduleRequestToJson(scheduleRequest);
        if (mappedRequest.isPresent()) {
            orchestratorController.postScheduleRequest(mappedRequest.get(), scheduleRequest.getScheduleType());
            return true;
        } else {
            throw new IllegalArgumentException();
        }
    }

}
