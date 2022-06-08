package com.swozo.api.orchestratorclient;

import com.swozo.config.Config;
import com.swozo.api.request.RequestSender;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;

@RestController
class OrchestratorController {
    String getActivityLinks(Long moduleActivityID) throws IOException {
        URL url = new URL(Config.ORCHESTRATOR_GET_URI + "/" + moduleActivityID);
        return RequestSender.sendGetRequest(url);
    }

    void postScheduleRequest(String jsonRequest) throws IOException {
        URL url = new URL(Config.ORCHESTRATOR_POST_URI);
        //possible to return orchestrator response in the future
        RequestSender.sendPost(url, jsonRequest);
    }
}
