package com.swozo.api.orchestratorclient;

import com.google.gson.Gson;
import com.swozo.jsonparser.ModelToJsonParser;
import com.swozo.model.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OrchestratorService {
    private final OrchestratorController orchestratorController;

    OrchestratorService(OrchestratorController orchestratorController) {
        this.orchestratorController = orchestratorController;
    }

    public OrchestratorLinkResponse getActivityLinks(Long activityModuleID) throws IOException {
        String jsonLinks = orchestratorController.getActivityLinks(activityModuleID);

        //TODO temporary version of mapping json to java class, need to coordinate with orchestrator response
        return new Gson().fromJson(jsonLinks, OrchestratorLinkResponse.class);
    }

    public boolean postScheduleRequest(ScheduleRequest scheduleRequest) throws IOException {
        //here goes mapping request to json
        String jsonRequest = ModelToJsonParser.mapScheduleRequestToJson(scheduleRequest);

        orchestratorController.postScheduleRequest(jsonRequest);
        return true;
    }

}
