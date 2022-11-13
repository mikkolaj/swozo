package com.swozo.orchestrator.scheduler;

import com.swozo.orchestrator.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class InternalTaskScheduler {
    private final ScheduledExecutorService executorService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConcurrentHashMap<Long, List<TaskDescription>> scheduleRequestsTasks;

    public InternalTaskScheduler(ApplicationProperties properties) {
        executorService = new ScheduledThreadPoolExecutor(properties.schedulerThreadPoolSize());
        scheduleRequestsTasks = new ConcurrentHashMap<>();
    }

    public <T> CompletableFuture<T> scheduleCancellableTask(long scheduleRequestId, Callable<T> task, long secondsOffset) {
        var futureResult = new CompletableFuture<T>();
        var scheduledTask = scheduleRegisteredExecution(scheduleRequestId, task, futureResult, secondsOffset);
        var currentScheduleRequestsTasks = getOrCreateCurrentListOfTasks(scheduleRequestId);
        currentScheduleRequestsTasks.add(new TaskDescription(task, scheduledTask));
        return futureResult;
    }

    public <T> CompletableFuture<T> schedule(Callable<T> task, long secondsOffset) {
        var futureResult = new CompletableFuture<T>();
        executorService.schedule(
                () -> completeResultAndExecuteCleanup(task, futureResult, () -> {
                }),
                secondsOffset,
                TimeUnit.SECONDS
        );
        return futureResult;
    }

    private <T> ScheduledFuture<?> scheduleRegisteredExecution(long scheduleRequestId, Callable<T> task, CompletableFuture<T> futureResult, long secondsOffset) {
        var cleanup = clearTaskEntry(scheduleRequestId, task);
        return executorService.schedule(
                () -> completeResultAndExecuteCleanup(task, futureResult, cleanup),
                secondsOffset,
                TimeUnit.SECONDS
        );
    }

    private <T> Runnable clearTaskEntry(long scheduleRequestId, Callable<T> task) {
        return () -> Optional.ofNullable(scheduleRequestsTasks.get(scheduleRequestId))
                .ifPresent(tasks -> forgetTask(scheduleRequestId, task, tasks));
    }

    private <T> void forgetTask(long scheduleRequestId, Callable<T> task, List<TaskDescription> tasks) {
        if (tasks.size() > 1) {
            tasks.removeIf(presentTask -> Objects.equals(presentTask.task, task));
        } else {
            scheduleRequestsTasks.remove(scheduleRequestId);
        }
    }

    public void cancelAllTasks(long scheduleRequestId) {
        Optional.ofNullable(scheduleRequestsTasks.get(scheduleRequestId))
                .ifPresent(tasks -> {
                    tasks.forEach(taskDescription -> taskDescription.executorsFuture.cancel(true));
                    scheduleRequestsTasks.remove(scheduleRequestId);
                });
    }

    private List<TaskDescription> getOrCreateCurrentListOfTasks(long scheduleRequestId) {
        return Optional.ofNullable(scheduleRequestsTasks.get(scheduleRequestId))
                .orElseGet(() -> createAndReturnNewList(scheduleRequestId));
    }

    private List<TaskDescription> createAndReturnNewList(long scheduleRequestId) {
        var newList = new ArrayList<TaskDescription>();
        scheduleRequestsTasks.put(scheduleRequestId, newList);
        return newList;
    }

    private <T> void completeResultAndExecuteCleanup(Callable<T> task, CompletableFuture<T> futureResult, Runnable cleanup) {
        try {
            futureResult.complete(task.call());
            cleanup.run();
        } catch (Throwable ex) {
            logger.error("Error while executing scheduled task.", ex);
            futureResult.completeExceptionally(ex);
        }

    }

    private record TaskDescription(Callable<?> task, ScheduledFuture<?> executorsFuture) {
    }
}
