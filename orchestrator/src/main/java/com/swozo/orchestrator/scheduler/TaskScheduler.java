package com.swozo.orchestrator.scheduler;

import com.swozo.orchestrator.configuration.EnvNames;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

@Service
public class TaskScheduler {
    private final ScheduledExecutorService executorService;

    public TaskScheduler(@Value("${" + EnvNames.SCHEDULER_THREAD_POOL_SIZE + "}") int threadPoolSize) {
        executorService = new ScheduledThreadPoolExecutor(threadPoolSize);
    }

    public void schedule(Runnable task, long secondsOffset) {
//        TODO: Scheduling and persistence
//        executorService.schedule(task, secondsOffset, TimeUnit.SECONDS);
        executorService.submit(task);
    }
}
