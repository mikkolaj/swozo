package com.swozo.api.orchestrator;

import com.swozo.config.Config;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
@Profile("!test")
@RequiredArgsConstructor
public class UriFactory {

    @Value("${orchestrator.server.url}")
    private final String orchestratorServerUrl;
    private static final String SEPARATOR = "/";

    public URI createActivityLinksURI(Long moduleActivityID) {
        return createURI(orchestratorServerUrl +
                Config.LINKS +
                SEPARATOR +
                moduleActivityID);
    }

    public URI createSchedulesUri() {
        return createURI(orchestratorServerUrl +
                Config.SCHEDULES);
    }

    public URI createAggregatedSchedulesUri() {
        return createURI(orchestratorServerUrl +
                Config.SCHEDULES + Config.AGGREGATED);
    }

    public URI createServiceConfigurationUri() {
        return createURI(orchestratorServerUrl +
                Config.SCHEDULES + Config.CONFIGURATION);
    }

    public URI createServiceConfigurationUri(String scheduleType) {
        return createURI(orchestratorServerUrl +
                Config.SCHEDULES + Config.CONFIGURATION + SEPARATOR + scheduleType);
    }

    @SneakyThrows(URISyntaxException.class)
    private URI createURI(String uri) {
        return new URI(uri);
    }
}
