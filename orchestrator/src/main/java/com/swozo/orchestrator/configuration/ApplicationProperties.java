package com.swozo.orchestrator.configuration;

import com.swozo.config.CloudProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@ConstructorBinding
@ConfigurationProperties
public record ApplicationProperties(
        Scheduler scheduler,
        Ansible ansible,
        Orchestrator orchestrator,
        CloudProvider cloudProvider,
        int systemCommandTimeoutMinutes
) {
    private record Scheduler(int threadPoolSize) {
    }

    private record Ansible(String playbookExecutablePath, String home) {
    }

    public record Orchestrator(String secret) {
    }

    public String ansiblePlaybookExecutablePath() {
        return ansible.playbookExecutablePath;
    }

    public String ansibleHome() {
        return ansible.home;
    }

    public int schedulerThreadPoolSize() {
        return scheduler.threadPoolSize;
    }
}
