package com.swozo.api.orchestratorclient;

import com.swozo.config.Config;
import com.swozo.api.request.RequestSender;
import com.swozo.model.scheduling.ScheduleType;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

@RestController
class OrchestratorController {
    String getActivityLinks(Long moduleActivityID) throws IOException {
        URL url = new URL(Config.HOST + Config.ORCHESTRATOR + Config.LINKS + "/" + moduleActivityID);
        return RequestSender.sendGetRequest(url);
    }

    void postScheduleRequest(String jsonRequest, ScheduleType scheduleType) throws IOException {
        URL url = new URL(Config.HOST + Config.ORCHESTRATOR + Config.SCHEDULE + "/" + scheduleType.name().toLowerCase(Locale.ROOT));
        //possible to return orchestrator response in the future
        RequestSender.sendPost(url, jsonRequest);
    }
}
