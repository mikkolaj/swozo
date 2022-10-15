package com.swozo.orchestrator.api.scheduling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.config.Config;
import com.swozo.model.scheduling.ScheduleRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;


@RestController
//@RequiredArgsConstructor
@RequestMapping(Config.SCHEDULES)
public class ScheduleController {
    private final RequestSender requestSender;
    private final ScheduleService service;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public ScheduleController(@Qualifier("web-server") RequestSender requestSender, ScheduleService service) {
        this.requestSender = requestSender;
        this.service = service;
    }

    @PostMapping
    public void schedule(@RequestBody ScheduleRequest request) {
        logger.info("Serving request: {}", request);
        service.schedule(request);
    }

    @GetMapping("/test-request-to-server")
    public void testRequestToServer() throws URISyntaxException, ExecutionException, InterruptedException {
        // TODO remove this one day
        requestSender.sendGet(new URI("http://localhost:5000/orchestrator-test"), new TypeReference<Void>(){}).get();
        System.out.println("DONE");
    }
}
