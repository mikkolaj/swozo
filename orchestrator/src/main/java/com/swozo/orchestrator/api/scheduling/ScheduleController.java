package com.swozo.orchestrator.api.scheduling;

import com.swozo.model.scheduling.ScheduleJupyter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService service;

    @Autowired
    public ScheduleController(ScheduleService service) {
        this.service = service;
    }

    @PostMapping("/jupyter")
    public void schedule(ScheduleJupyter request) {
        service.schedule(request);
    }
}
