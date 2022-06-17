package com.swozo.orchestrator.configuration;

import com.swozo.orchestrator.configuration.conditions.CloudProvider;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties
public record ApplicationProperties(
        Scheduler scheduler,
        Ansible ansible,
        CloudProvider cloudProvider,
        int systemCommandTimeoutMinutes
) {
    private record Scheduler(int threadPoolSize) {
    }

    private record Ansible(Jupyter jupyter, int maxTimeoutMinutes) {
        private record Jupyter(String playbookPath) {
        }
    }

    public int ansibleMaxTimeoutMinutes() {
        return ansible.maxTimeoutMinutes;
    }

    public String jupyterPlaybookPath() {
        return ansible.jupyter.playbookPath;
    }

    public int schedulerThreadPoolSize() {
        return scheduler.threadPoolSize;
    }
}
