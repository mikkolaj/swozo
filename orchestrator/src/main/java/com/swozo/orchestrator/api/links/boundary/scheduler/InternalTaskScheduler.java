package com.swozo.orchestrator.api.links.boundary.scheduler;

import com.swozo.orchestrator.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.concurrent.*;

@Service
public class InternalTaskScheduler {
    private final ScheduledExecutorService executorService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public InternalTaskScheduler(ApplicationProperties properties) {
        executorService = new ScheduledThreadPoolExecutor(properties.schedulerThreadPoolSize());
    }

    public <T> CompletableFuture<T> schedule(Callable<T> task, long secondsOffset) {
        var futureResult = new CompletableFuture<T>();
        executorService.schedule(() -> wrapWithLogs(task, futureResult), secondsOffset, TimeUnit.SECONDS);
        return futureResult;
    }

    private <T> void wrapWithLogs(Callable<T> task, CompletableFuture<T> futureResult) {
        try {
            futureResult.complete(task.call());
        } catch (Throwable ex) {
            logger.error("Error while executing scheduled task.", ex);
            futureResult.completeExceptionally(ex);
        }
    }

}
