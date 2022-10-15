package com.swozo.orchestrator.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.swozo.communication.http.JsonRequestSender;
import com.swozo.communication.http.RequestSender;
import com.swozo.communication.http.decorators.RequestSenderEnhancerDecorator;
import com.swozo.communication.http.decorators.RequestSenderSecretKeyHeaderDecorator;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.utils.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommunicationConfig {
    private final ObjectMapper mapper;
    private final ApplicationProperties applicationProperties;

    @Bean(name = "web-server")
    public RequestSender provideWebServerRequestSender() {
        var requestSender = new JsonRequestSender(new JsonMapperFacade(mapper));
        return new RequestSenderEnhancerDecorator(
                new RequestSenderSecretKeyHeaderDecorator(
                        requestSender,
                        () -> applicationProperties.orchestrator().secret()
                ),
                ServiceType.WEB,
                3
            );
    }
}
