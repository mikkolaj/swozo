package com.swozo.orchestrator.api.scheduling.boundary;

import com.swozo.config.Config;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.scheduling.control.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequiredArgsConstructor
@RequestMapping(Config.SCHEDULES)
public class ScheduleController {
    private final ScheduleService service;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping
    public long schedule(@RequestBody ScheduleRequest request) {
        logger.info("Serving request: {}", request);
        return service.schedule(request);
    }
}
