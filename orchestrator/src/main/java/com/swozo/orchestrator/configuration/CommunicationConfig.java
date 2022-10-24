package com.swozo.orchestrator.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swozo.communication.http.JsonRequestSender;
import com.swozo.communication.http.RequestSender;
import com.swozo.communication.http.decorators.RequestSenderEnhancerDecorator;
import com.swozo.communication.http.decorators.RequestSenderHeaderDecorator;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.utils.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;

@Configuration
@RequiredArgsConstructor
public class CommunicationConfig {
    private final ObjectMapper mapper;
    private final ApplicationProperties applicationProperties;

    @Bean(name = "web-server")
    public RequestSender provideWebServerRequestSender() {
        var requestSender = new JsonRequestSender(new JsonMapperFacade(mapper));
        final var BACKOFF_RETRIES = 3;

        return new RequestSenderEnhancerDecorator(
                new RequestSenderHeaderDecorator(
                        requestSender,
                        builder -> builder.header(HttpHeaders.AUTHORIZATION, applicationProperties.orchestrator().secret())
                ),
                ServiceType.WEB,
                BACKOFF_RETRIES
            );
    }
}
