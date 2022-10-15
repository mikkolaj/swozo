package com.swozo.orchestrator.configuration;

import com.swozo.config.CloudProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

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

    private record Ansible(Jupyter jupyter) {
        private record Jupyter(String playbookPath) {
        }
    }

    public record Orchestrator(String secret) {
    }

    public String jupyterPlaybookPath() {
        return ansible.jupyter.playbookPath;
    }

    public int schedulerThreadPoolSize() {
        return scheduler.threadPoolSize;
    }
}
