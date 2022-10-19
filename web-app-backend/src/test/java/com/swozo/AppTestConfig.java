package com.swozo;

import com.swozo.api.common.files.storage.StorageProvider;
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
}
