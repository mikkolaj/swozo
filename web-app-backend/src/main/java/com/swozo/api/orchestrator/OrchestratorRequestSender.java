package com.swozo.api.orchestrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.swozo.communication.http.RequestSender.unwrap;

@Component
class OrchestratorRequestSender {
    private final RequestSender requestSender;
    private final UriFactory uriFactory;

    @Autowired
    public OrchestratorRequestSender(@Qualifier("orchestrator") RequestSender requestSender, UriFactory uriFactory) {
        this.requestSender = requestSender;
        this.uriFactory = uriFactory;
    }

    public CompletableFuture<OrchestratorLinkResponse> getActivityLinks(Long scheduleRequestId) {
        var uri = uriFactory.createActivityLinksURI(scheduleRequestId);
        return unwrap(requestSender.sendGet(uri, new TypeReference<>() {}));
    }

    public CompletableFuture<ScheduleResponse> sendScheduleRequest(ScheduleRequest scheduleRequest) {
        var uri = uriFactory.createSchedulesUri();
        return unwrap(requestSender.sendPost(uri, scheduleRequest,  new TypeReference<>() {}));
    }

    public CompletableFuture<Collection<ScheduleResponse>> sendScheduleRequests(Collection<ScheduleRequest> scheduleRequests) {
        var uri = uriFactory.createAggregatedSchedulesUri();
        return unwrap(requestSender.sendPost(uri, scheduleRequests,  new TypeReference<>() {}));
    }

    public CompletableFuture<List<ServiceConfig>> getServiceConfigs() {
        var uri = uriFactory.createServiceConfigurationUri();
        return unwrap(requestSender.sendGet(uri, new TypeReference<>(){}));
    }
}
