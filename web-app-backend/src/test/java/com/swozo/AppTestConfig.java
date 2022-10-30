package com.swozo;

import com.swozo.api.common.files.storage.StorageProvider;
import com.swozo.api.orchestrator.OrchestratorRequestSender;
import com.swozo.api.orchestrator.UriFactory;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class AppTestConfig {
    @Bean
    @Primary
    StorageProvider mockStorageProvider() {
        return Mockito.mock(StorageProvider.class);
    }

    @Bean
    @Primary
    OrchestratorRequestSender mockOrchestratorRequestSender() {
        return Mockito.mock(OrchestratorRequestSender.class);
    }

    @Bean
    @Primary
    UriFactory mockUriFactory() {
        return Mockito.mock(UriFactory.class);
    }
}
