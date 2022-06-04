package com.swozo.orchestrator.api.scheduling;

import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduling")
public class SchedulingController {
    private final SchedulingService service;

    @Autowired
    public SchedulingController(SchedulingService service) {
        this.service = service;
    }

    @PostMapping("schedule")
    public void schedule(ScheduleRequest request) {
        service.schedule(request);
    }
}
