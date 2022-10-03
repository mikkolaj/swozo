package com.swozo.orchestrator.api.scheduling.boundary;

import com.swozo.config.Config;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.orchestrator.api.scheduling.control.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;


@RestController
@RequiredArgsConstructor
@RequestMapping(Config.SCHEDULES)
public class ScheduleController {
    private final ScheduleService service;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping
    public ScheduleResponse schedule(@RequestBody ScheduleRequest request) {
        logger.info("Serving request: {}", request);
        return service.schedule(request);
    }

    @PostMapping(Config.AGGREGATED)
    public Collection<ScheduleResponse> schedule(@RequestBody Collection<ScheduleRequest> requests) {
        logger.info("Serving aggregated request: {}", requests);
        return requests.stream().map(service::schedule).toList();
    }
}
