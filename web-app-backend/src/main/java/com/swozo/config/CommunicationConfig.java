package com.swozo.config;

import com.swozo.communication.http.JsonRequestSender;
import com.swozo.communication.http.RequestSender;
import com.swozo.communication.http.decorators.RequestSenderEnhancerDecorator;
import com.swozo.jsonmapper.JsonMapperFacade;
import com.swozo.utils.ServiceType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommunicationConfig {
   private final JsonMapperFacade jsonMapperFacade;

    @Bean(name = "orchestrator")
    public RequestSender provideOrchestratorRequestSender() {
        var requestSender = new JsonRequestSender(jsonMapperFacade);
        return new RequestSenderEnhancerDecorator(requestSender, ServiceType.ORCHESTRATOR, 3);
    }
}
