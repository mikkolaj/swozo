package com.swozo.orchestrator.api.backend;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;

import static com.swozo.config.Config.*;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class BackendUriFactory {
    @Value("${backend.server.url}")
    private final String backendUrl;
    private static final String SEPARATOR = "/";

    @SneakyThrows
    public URI createLinksUri(long scheduleRequestId) {
        var endpoint = backendUrl +
                ACTIVITIES +
                INTERNAL +
                LINKS +
                withSep(scheduleRequestId);
        return new URI(endpoint);
    }

    @SneakyThrows
    public URI createUploadInitUri(long activityModuleId, long userId) {
        var endpoint = backendUrl +
                ACTIVITIES +
                INTERNAL +
                INIT_UPLOAD +
                withSep(activityModuleId) +
                withSep(userId);

        return new URI(endpoint);
    }

    @SneakyThrows
    public URI createUploadAckUri(long activityModuleId, long scheduleRequestId, long userId) {
        var endpoint = backendUrl +
                ACTIVITIES +
                INTERNAL +
                ACK_UPLOAD +
                withSep(activityModuleId) +
                withSep(scheduleRequestId) +
                withSep(userId);

        return new URI(endpoint);
    }

    @SneakyThrows
    public URI createDownloadUri(String encodedFileIdentifier) {
        var endpoint = backendUrl +
                FILES +
                INTERNAL +
                DOWNLOAD +
                withSep(encodedFileIdentifier);
        return new URI(endpoint);
    }

    @SneakyThrows
    public URI createUserDataUrl(long activityModuleId, long scheduleRequestId) {
        var endpoint = backendUrl +
                ACTIVITIES +
                INTERNAL +
                USERS +
                withSep(activityModuleId) +
                withSep(scheduleRequestId);
        return new URI(endpoint);
    }

    private String withSep(@NotNull Long id) {
        return withSep(id.toString());
    }

    private String withSep(String text) {
        return SEPARATOR + text;
    }
}
