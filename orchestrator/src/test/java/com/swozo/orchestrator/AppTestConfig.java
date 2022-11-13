package com.swozo.orchestrator;


import com.swozo.communication.http.RequestSender;
import com.swozo.orchestrator.api.backend.BackendRequestSender;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AppTestConfig {

    @Bean()
    public BackendRequestSender provideBackendRequestSender() {
        return Mockito.mock(BackendRequestSender.class);
    }

    @Bean(name = "web-server")
    @Primary
    public RequestSender provideWebServerRequestSender() {
        return Mockito.mock(RequestSender.class);
    }
}
