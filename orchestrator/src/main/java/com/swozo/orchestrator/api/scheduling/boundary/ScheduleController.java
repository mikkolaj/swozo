package com.swozo.orchestrator.api.scheduling.boundary;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.ServiceType;
import com.swozo.orchestrator.api.scheduling.control.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static com.swozo.config.Config.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(SCHEDULES)
public class ScheduleController {
    private final ScheduleService service;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping
    public ScheduleResponse schedule(@RequestBody ScheduleRequest request) {
        logger.info("Serving request: {}", request);
        return service.schedule(request);
    }

    @DeleteMapping("/{scheduleRequestId}")
    public void cancel(@PathVariable long scheduleRequestId) {
        logger.info("Serving cancellation request for schedule request [id: {}]", scheduleRequestId);
        service.cancel(scheduleRequestId);
    }

    @GetMapping(CONFIGURATION)
    public List<ServiceConfig> getSupportedServices() {
        logger.info("Serving config request.");
        return service.getSupportedServices();
    }

    @GetMapping(CONFIGURATION + "/{scheduleType}")
    public ServiceConfig getServiceConfig(@PathVariable String scheduleType) {
        logger.info("Serving config request for {}", scheduleType);
        return service.getServiceConfig(ServiceType.valueOf(scheduleType));
    }

    @PostMapping(AGGREGATED)
    public List<ScheduleResponse> schedule(@RequestBody Collection<ScheduleRequest> requests) {
        logger.info("Serving aggregated request: {}", requests);
        return requests.stream().map(service::schedule).toList();
    }
}
