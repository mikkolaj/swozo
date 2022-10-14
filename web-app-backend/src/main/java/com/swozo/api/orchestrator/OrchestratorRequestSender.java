package com.swozo.api.orchestrator;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.api.orchestrator.exceptions.InvalidStatusCodeException;
import com.swozo.api.orchestrator.exceptions.ServiceUnavailableException;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.model.links.OrchestratorLinkResponse;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.util.ServiceType;
import com.swozo.utils.RequestSender;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.http.HttpResponse;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
class OrchestratorRequestSender {
    private final RequestSender requestSender;
    private final UriFactory uriFactory;
    private final JsonMapperFacade mapper;


    public CompletableFuture<OrchestratorLinkResponse> getActivityLinks(Long scheduleRequestId) {
        var uri = uriFactory.createActivityLinksURI(scheduleRequestId);
        // TODO proper error handling
        return withOkStatusAssertion(requestSender.sendGet(uri))
                .thenApply(response -> mapper.fromJson(response.body(), OrchestratorLinkResponse.class));
    }

    public CompletableFuture<ScheduleResponse> sendScheduleRequest(ScheduleRequest scheduleRequest) {
        var uri = uriFactory.createSchedulesUri();
        return withOkStatusAssertion(requestSender.sendPost(uri, mapper.toJson(scheduleRequest)))
                .thenApply(response -> mapper.fromJson(response.body(), ScheduleResponse.class));
    }

    public CompletableFuture<Collection<ScheduleResponse>> sendScheduleRequests(Collection<ScheduleRequest> scheduleRequests) {
        var uri = uriFactory.createAggregatedSchedulesUri();
        return withOkStatusAssertion(requestSender.sendPost(uri, mapper.toJson(scheduleRequests)))
                .thenApply(response -> mapper.fromJson(response.body(), new TypeReference<>() {}));
    }

    private <T> CompletableFuture<HttpResponse<T>> withOkStatusAssertion(CompletableFuture<HttpResponse<T>> response) {
        return response.exceptionally(ex -> {
                    throw new ServiceUnavailableException(ServiceType.ORCHESTRATOR, ex);
                })
                .thenApply(resp -> {
                    if (!HttpStatus.valueOf(resp.statusCode()).is2xxSuccessful())
                        throw new InvalidStatusCodeException(resp);
                    return resp;
                });
    }
}
