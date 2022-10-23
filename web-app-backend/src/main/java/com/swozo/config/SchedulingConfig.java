package com.swozo.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulingConfig {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    public ThreadPoolTaskScheduler provideScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setErrorHandler(err -> logger.error("Scheduled task failed", err));
        scheduler.setPoolSize(3);
        return scheduler;
    }
}
