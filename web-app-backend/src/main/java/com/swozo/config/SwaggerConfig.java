package com.swozo.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    public static final String ACCESS_TOKEN = "JWT_AUTH";

    @Bean
    public OpenAPI configureApi() {
        return new OpenAPI().components(
                new Components()
                        .addSecuritySchemes(ACCESS_TOKEN, new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT"))
        );
    }

    @Bean
    public GroupedOpenApi webApiSpec() {
        String[] pathsToMatch = {
                "/auth/**",
                "/users/**",
                "/service-modules/**",
                "/courses/**",
                "/activities/**"
        };

        String[] pathsToExclude = {
                "/courses/all-system-courses",
                "/service-modules/all-system-modules"
        };

        return GroupedOpenApi.builder()
                .group("web")
                .pathsToMatch(pathsToMatch)
                .pathsToExclude(pathsToExclude)
                .build();
    }

}
