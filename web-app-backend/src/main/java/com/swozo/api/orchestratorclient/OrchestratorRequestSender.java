package com.swozo.api.orchestratorclient;

import com.swozo.config.Config;
import com.swozo.api.requestsender.RequestSender;
import com.swozo.model.scheduling.ScheduleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

@Component
class OrchestratorRequestSender {
    @Value("${orchestrator.server.url}")
    private String orchestratorServerUrl;

    String getActivityLinks(Long moduleActivityID) throws IOException {
        var url = new URL(orchestratorServerUrl +
                Config.ORCHESTRATOR +
                Config.LINKS +
                "/" +
                moduleActivityID);

        return RequestSender.sendGet(url);
    }

    void postScheduleRequest(String jsonScheduleRequest, ScheduleType scheduleType) throws IOException {
        var url = new URL(orchestratorServerUrl +
                Config.ORCHESTRATOR +
                Config.SCHEDULE +
                "/" +
                scheduleType.name().toLowerCase(Locale.ROOT));
        //possible to return orchestrator response in the future
        RequestSender.sendPost(url, jsonScheduleRequest);
    }
}
