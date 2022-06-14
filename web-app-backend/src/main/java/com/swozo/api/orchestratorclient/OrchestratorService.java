package com.swozo.api.orchestratorclient;

import com.swozo.jsonmapper.JsonMapper;
import com.swozo.model.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Optional;

@Service
public class OrchestratorService {
    private final OrchestratorController orchestratorController;

    OrchestratorService(OrchestratorController orchestratorController) {
        this.orchestratorController = orchestratorController;
    }

    public OrchestratorLinkResponse getActivityLinks(Long activityModuleID) throws IOException, IllegalArgumentException {
        String jsonLinks = orchestratorController.getActivityLinks(activityModuleID);

        Optional<OrchestratorLinkResponse> response = JsonMapper.mapJsonToLinkResponse(jsonLinks);

        if (response.isPresent()) {
            return response.get();
        } else {
            //not sure what to throw here
            throw new IllegalArgumentException();
        }
    }

    public boolean postScheduleRequest(ScheduleRequest scheduleRequest) {
        Optional<String> mappedRequest = JsonMapper.mapScheduleRequestToJson(scheduleRequest);
        if (mappedRequest.isPresent()) {
            try {
                orchestratorController.postScheduleRequest(mappedRequest.get(), scheduleRequest.getScheduleType());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

}
