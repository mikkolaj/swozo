package com.swozo.orchestrator.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

@EnableAsync
@Configuration
public class SpringConfig {
    @Bean
    @ConditionalOnProperty(
            value = "logging.log-requests",
            havingValue = "true"
    )
    public CommonsRequestLoggingFilter logFilter() {
        var filter = new CommonsRequestLoggingFilter();
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("Request: ");
        filter.setAfterMessageSuffix(" ");
        return filter;
    }
}
