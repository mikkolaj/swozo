package com.swozo.api.orchestrator;

import com.swozo.config.Config;
import com.swozo.model.scheduling.properties.ScheduleType;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

@Component
public class UriFactory {
    private final String orchestratorServerUrl;

    @Autowired
    public UriFactory(@Value("${orchestrator.server.url}") String orchestratorServerUrl) {
        this.orchestratorServerUrl = orchestratorServerUrl;
    }

    public URI createActivityLinksURI(Long moduleActivityID) {
        return createURI(orchestratorServerUrl +
                Config.LINKS +
                "/" +
                moduleActivityID);
    }

    public URI createSchedulesUri(ScheduleType scheduleType) {
        return createURI(orchestratorServerUrl +
                Config.SCHEDULES);
    }

    @SneakyThrows(URISyntaxException.class)
    private URI createURI(String uri) {
        return new URI(uri);
    }
}
