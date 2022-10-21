package com.swozo.config;


import com.swozo.security.keys.EnvKeyProvider;
import com.swozo.security.keys.KeyProvider;
import com.swozo.utils.ServiceType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KeysConfig {
    @Bean
    @Autowired
    public KeyProvider keyProvider(EnvKeyProvider provider) {
        assertValidKeySizes(provider);
        return provider;
    }

    private void assertValidKeySizes(KeyProvider keyProvider) {
        assertKeySize(keyProvider.getJwtSecretKey(), "JWT", 64);
        assertKeySize(keyProvider.getServiceSecretKey(ServiceType.ORCHESTRATOR), "Orchestrator", 32);
    }

    private void assertKeySize(byte[] key, String keyName, int minLength) {
        if (key.length < minLength) {
            throw new IllegalArgumentException("Min length for \"" + keyName + " key\" is " + minLength);
        }
    }
}
