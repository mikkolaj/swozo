package com.swozo.config.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties
public record ApplicationProperties(InitialAdmin initialAdmin) {
    public record InitialAdmin(String email, String name, String surname) {
    }
}
