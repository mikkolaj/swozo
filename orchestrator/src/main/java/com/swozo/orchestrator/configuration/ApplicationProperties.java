package com.swozo.orchestrator.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties
public record ApplicationProperties(
        Scheduler scheduler,
        Ansible ansible,
        CloudProvider cloudProvider
) {
    private record Scheduler(int threadPoolSize) {
    }

    private record Ansible(Jupyter jupyter) {
        private record Jupyter(String playbookPath) {
        }
    }

    public String jupyterPlaybookPath() {
        return ansible.jupyter.playbookPath;
    }

    public int schedulerThreadPoolSize() {
        return scheduler.threadPoolSize;
    }
}
