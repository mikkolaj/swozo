package com.swozo.api.orchestratorclient;

import com.swozo.api.requestsender.UriFactory;
import com.swozo.api.requestsender.RequestSender;
import com.swozo.model.scheduling.properties.ScheduleType;
import org.springframework.stereotype.Component;

@Component
class OrchestratorRequestSender {
    private final RequestSender requestSender;
    private final UriFactory uriFactory;

    OrchestratorRequestSender(RequestSender requestSender, UriFactory uriFactory) {
        this.requestSender = requestSender;
        this.uriFactory = uriFactory;
    }

    String getActivityLinks(Long moduleActivityID) {
        var uri = uriFactory.createActivityLinksURI(moduleActivityID);
        return requestSender.sendGet(uri).body();
    }

    void postScheduleRequest(String jsonScheduleRequest, ScheduleType scheduleType) {
        var uri = uriFactory.createSchedulesUri(scheduleType);
        //possible to return orchestrator response in the future
        requestSender.sendPost(uri, jsonScheduleRequest);
    }
}
