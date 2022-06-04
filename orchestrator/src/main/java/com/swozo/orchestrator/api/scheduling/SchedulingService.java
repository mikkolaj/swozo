package com.swozo.orchestrator.api.scheduling;

import com.swozo.model.scheduling.ScheduleJupyter;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.scheduler.TaskScheduler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SchedulingService {
    private final TaskScheduler scheduler;

    @Autowired
    public SchedulingService(TaskScheduler scheduler) {
        this.scheduler = scheduler;
    }

    public void schedule(ScheduleRequest request) {
        switch (request) {
            case ScheduleJupyter jupyterRequest -> scheduler.schedule(() -> System.out.println("HI"), 1);
            default -> throw new IllegalArgumentException("Unsupported request type: " + request);
        }
    }
}
