package com.swozo.orchestrator.api.scheduling;

import com.swozo.model.scheduling.ScheduleRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping("/schedules")
public class ScheduleController {
    private final ScheduleService service;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping
    public void schedule(@RequestBody ScheduleRequest request) {
        logger.info("Serving request: {}", request);
        service.schedule(request);
    }
}
