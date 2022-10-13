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

    private record Ansible(String playbookExecutablePath, Jupyter jupyter) {
        private record Jupyter(String playbookPath) {
        }
    }

    public String ansiblePlaybookExecutablePath() {
        return ansible.playbookExecutablePath;
    }

    public String jupyterPlaybookPath() {
        return ansible.jupyter.playbookPath;
    }

    public int schedulerThreadPoolSize() {
        return scheduler.threadPoolSize;
    }
}
