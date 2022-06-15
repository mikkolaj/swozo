package com.swozo.api.orchestratorclient;

import com.swozo.api.exceptions.ServiceType;
import com.swozo.api.exceptions.ServiceUnavailableException;
import com.swozo.api.requestsender.UriFactory;
import com.swozo.api.requestsender.RequestSender;
import com.swozo.jsonmapper.JsonMapper;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
class OrchestratorRequestSender {
    private final RequestSender requestSender;
    private final UriFactory uriFactory;

    @Autowired
    OrchestratorRequestSender(RequestSender requestSender, UriFactory uriFactory) {
        this.requestSender = requestSender;
        this.uriFactory = uriFactory;
    }

    OrchestratorLinkResponse getActivityLinks(Long moduleActivityID) {
        var uri = uriFactory.createActivityLinksURI(moduleActivityID);
        var jsonLinks = requestSender.sendGet(uri).body();
        Optional<OrchestratorLinkResponse> response = JsonMapper.mapJsonToLinkResponse(jsonLinks);

        return response
                .orElseThrow(() -> new ServiceUnavailableException(ServiceType.ORCHESTRATOR));
    }

    void postScheduleRequest(ScheduleRequest scheduleRequest) {
        var uri = uriFactory.createSchedulesUri(scheduleRequest.getScheduleType());
        Optional<String> jsonScheduleRequest = JsonMapper.mapScheduleRequestToJson(scheduleRequest);
        if (jsonScheduleRequest.isPresent()) {
            requestSender.sendPost(uri, jsonScheduleRequest.get());
        } else {
            throw new IllegalArgumentException();
        }
    }
}
