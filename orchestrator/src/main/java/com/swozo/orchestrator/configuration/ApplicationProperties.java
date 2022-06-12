package com.swozo.orchestrator.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ApplicationProperties {
    public final int schedulerThreadPoolSize;
    public final String jupyterPlaybookPath;

    public ApplicationProperties(
            @Value("${" + EnvNames.SCHEDULER_THREAD_POOL_SIZE + "}") int schedulerThreadPoolSize,
            @Value("${" + EnvNames.ANSIBLE_JUPYTER_PLAYBOOK_PATH + "}") String jupyterPlaybookPath
    ) {
        this.schedulerThreadPoolSize = schedulerThreadPoolSize;
        this.jupyterPlaybookPath = jupyterPlaybookPath;
    }
}
