package com.swozo.orchestrator.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.config.Config;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.utils.CheckedExceptionConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.swozo.communication.http.RequestSender.unwrap;

@Component
@Profile("!test")
public class BackendRequestSender {
    private static final String SEPARATOR = "/";
    private final RequestSender requestSender;
    private final String backendUrl;

    @Autowired
    public BackendRequestSender(@Qualifier("web-server") RequestSender requestSender, @Value("${backend.server.url}") String backendUrl) {
        this.requestSender = requestSender;
        this.backendUrl = backendUrl;
    }

    public CompletableFuture<Void> putActivityLinks(Long scheduleRequestId, List<ActivityLinkInfo> links) {
        return unwrap(requestSender.sendPut(createLinksUri(scheduleRequestId), links, new TypeReference<>() {
        }));
    }

    private URI createLinksUri(long scheduleRequestId) {
        var endpoint = backendUrl +
                Config.ACTIVITIES +
                Config.INTERNAL +
                Config.LINKS +
                SEPARATOR +
                scheduleRequestId;

        try {
            return new URI(endpoint);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
