package com.swozo.api.requestsender;

import com.swozo.config.Config;
import com.swozo.model.scheduling.properties.ScheduleType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
/*
    We might want to create links to communicate not only with orchestrator, so I placed class in this package
 */

@Component
public class UriFactory {
    @Value("${orchestrator.server.url}")
    private String orchestratorServerUrl;

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

    private URI createURI(String uri) {
        try {
            return new URI(uri);
        } catch (URISyntaxException e) {
            //this will never be thrown :))))
            throw new IllegalArgumentException();
        }
    }
}
