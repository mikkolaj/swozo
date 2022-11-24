package com.swozo.orchestrator.scheduler;

import com.swozo.orchestrator.configuration.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.*;

@Service
public class InternalTaskScheduler {
    private final ScheduledExecutorService executorService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConcurrentHashMap<Long, ConcurrentLinkedQueue<TaskDescription>> scheduleRequestsTasks;

    public InternalTaskScheduler(ApplicationProperties properties) {
        executorService = new ScheduledThreadPoolExecutor(properties.schedulerThreadPoolSize());
        scheduleRequestsTasks = new ConcurrentHashMap<>();
    }

    public <T> CompletableFuture<T> scheduleCancellableTask(long scheduleRequestId, Callable<T> task, long secondsOffset) {
        var futureResult = new CompletableFuture<T>();
        var scheduledTask = scheduleRegisteredExecution(scheduleRequestId, task, futureResult, secondsOffset);
        registerScheduleRequestSubtask(scheduleRequestId, new TaskDescription(task, futureResult, scheduledTask));
        return futureResult;
    }

    private void registerScheduleRequestSubtask(long scheduleRequestId, TaskDescription subTask) {
        var currentScheduleRequestsTasks = getOrCreateCurrentListOfTasks(scheduleRequestId);
        currentScheduleRequestsTasks.add(subTask);
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
        return executorService.schedule(
                () -> completeResultAndExecuteCleanup(task, futureResult, () -> clearTaskEntry(scheduleRequestId, task)),
                secondsOffset,
                TimeUnit.SECONDS
        );
    }

    private <T> void clearTaskEntry(long scheduleRequestId, Callable<T> task) {
        Optional.ofNullable(scheduleRequestsTasks.get(scheduleRequestId))
                .ifPresent(tasks -> forgetTask(scheduleRequestId, task, tasks));
    }

    private <T> void forgetTask(long scheduleRequestId, Callable<T> task, ConcurrentLinkedQueue<TaskDescription> tasks) {
        if (tasks.size() > 1) {
            tasks.removeIf(presentTask -> Objects.equals(presentTask.task, task));
        } else {
            scheduleRequestsTasks.remove(scheduleRequestId);
        }
    }

    public void cancelAllTasks(long scheduleRequestId) {
        Optional.ofNullable(scheduleRequestsTasks.get(scheduleRequestId))
                .ifPresent(tasks -> {
                    tasks.forEach(taskDescription -> {
                        taskDescription.executorsFuture.cancel(true);
                        taskDescription.futureResult.cancel(true);
                    });
                    scheduleRequestsTasks.remove(scheduleRequestId);
                });
    }

    private ConcurrentLinkedQueue<TaskDescription> getOrCreateCurrentListOfTasks(long scheduleRequestId) {
        return Optional.ofNullable(scheduleRequestsTasks.get(scheduleRequestId))
                .orElseGet(() -> createNewList(scheduleRequestId));
    }

    private ConcurrentLinkedQueue<TaskDescription> createNewList(long scheduleRequestId) {
        var newList = new ConcurrentLinkedQueue<TaskDescription>();
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

    @PreDestroy
    public void destroy() {
        executorService.shutdown();
    }

    private record TaskDescription(
            Callable<?> task,
            CompletableFuture<?> futureResult,
            ScheduledFuture<?> executorsFuture
    ) {
    }
}
