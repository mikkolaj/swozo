package com.swozo.orchestrator.scheduler;

import com.swozo.orchestrator.configuration.ApplicationProperties;
import org.springframework.stereotype.Service;

import java.util.concurrent.Callable;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class InternalTaskScheduler {
    private final ScheduledExecutorService executorService;

    public InternalTaskScheduler(ApplicationProperties properties) {
        executorService = new ScheduledThreadPoolExecutor(properties.schedulerThreadPoolSize());
    }

    public void schedule(Callable<Void> task, long secondsOffset) {
        executorService.schedule(task, secondsOffset, TimeUnit.SECONDS);
    }

}
