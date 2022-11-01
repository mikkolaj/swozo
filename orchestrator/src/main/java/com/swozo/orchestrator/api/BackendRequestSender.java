package com.swozo.orchestrator.api;


import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.utils.StorageAccessRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.swozo.communication.http.RequestSender.unwrap;
import static com.swozo.config.Config.*;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class BackendRequestSender {
    private static final String SEPARATOR = "/";
    @Qualifier("web-server")
    private final RequestSender requestSender;
    @Value("${backend.server.url}")
    private final String backendUrl;

    public CompletableFuture<Void> putActivityLinks(Long scheduleRequestId, List<ActivityLinkInfo> links) {
        return unwrap(requestSender.sendPut(createLinksUri(scheduleRequestId), links, new TypeReference<>() {
        }));
    }

    public CompletableFuture<StorageAccessRequest> getSignedDownloadUrl(String encodedFileIdentifier) {
        return unwrap(requestSender.sendGet(createDownloadUri(encodedFileIdentifier), new TypeReference<>() {
        }));
    }

    @SneakyThrows
    private URI createLinksUri(long scheduleRequestId) {
        var endpoint = backendUrl +
                ACTIVITIES +
                INTERNAL +
                LINKS +
                SEPARATOR +
                scheduleRequestId;
        return new URI(endpoint);
    }

    @SneakyThrows
    private URI createDownloadUri(String encodedFileIdentifier) {
        var endpoint = backendUrl +
                FILES +
                INTERNAL +
                DOWNLOAD +
                SEPARATOR +
                encodedFileIdentifier;
        return new URI(endpoint);
    }
}
