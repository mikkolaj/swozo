package com.swozo.api.orchestrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

import static com.swozo.communication.http.RequestSender.unwrap;

@Component
class OrchestratorRequestSender {
    @Qualifier("orchestrator")
    private final RequestSender requestSender;
    private final UriFactory uriFactory;

    @Autowired
    public OrchestratorRequestSender(@Qualifier("orchestrator") RequestSender requestSender, UriFactory uriFactory) {
        this.requestSender = requestSender;
        this.uriFactory = uriFactory;
    }

    public CompletableFuture<OrchestratorLinkResponse> getActivityLinks(Long moduleActivityID) {
        var uri = uriFactory.createActivityLinksURI(moduleActivityID);
        return unwrap(requestSender.sendGet(uri, new TypeReference<>() {}));
    }

    public CompletableFuture<Void> sendScheduleRequest(ScheduleRequest scheduleRequest) {
        var uri = uriFactory.createSchedulesUri(scheduleRequest.getScheduleType());
        return unwrap(requestSender.sendPost(uri, scheduleRequest, new TypeReference<>() {}));
    }
}
