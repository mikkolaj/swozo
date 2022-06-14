package com.swozo.api.orchestratorclient;

import com.swozo.config.Config;
import com.swozo.api.requestsender.RequestSender;
import com.swozo.model.scheduling.ScheduleType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;

@Component
class OrchestratorRequestSender {
    String getActivityLinks(Long moduleActivityID) throws IOException {
        URL url = new URL(Config.HOST +
                Config.ORCHESTRATOR +
                Config.LINKS +
                "/" +
                moduleActivityID);

        return RequestSender.sendGet(url);
    }

    void postScheduleRequest(String jsonScheduleRequest, ScheduleType scheduleType) throws IOException {
        URL url = new URL(Config.HOST +
                Config.ORCHESTRATOR +
                Config.SCHEDULE +
                "/" +
                scheduleType.name().toLowerCase(Locale.ROOT));
        //possible to return orchestrator response in the future
        RequestSender.sendPost(url, jsonScheduleRequest);
    }
}
