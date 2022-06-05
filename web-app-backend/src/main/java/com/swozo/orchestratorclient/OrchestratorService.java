package com.swozo.orchestratorclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.swozo.model.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class OrchestratorService {
    @Autowired
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
        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        String jsonRequest = ow.writeValueAsString(scheduleRequest);

        orchestratorController.postScheduleRequest(jsonRequest);
        return true;
    }

}
