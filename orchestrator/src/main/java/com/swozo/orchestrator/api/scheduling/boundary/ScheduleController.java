package com.swozo.orchestrator.api.scheduling.boundary;

import com.fasterxml.jackson.core.type.TypeReference;
import com.swozo.communication.http.RequestSender;
import com.swozo.i18n.TranslationsProvider;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.orchestrator.api.BackendRequestSender;
import com.swozo.orchestrator.api.scheduling.control.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.swozo.config.Config.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(SCHEDULES)
public class ScheduleController {
    private final ScheduleService service;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final TranslationsProvider translationsProvider;
    private final BackendRequestSender backendRequestSender;
    @Qualifier("web-server")
    private final RequestSender requestSender;
    @Value("${backend.server.url}")
    private final String backendUrl;

    @PostMapping
    public ScheduleResponse schedule(@RequestBody ScheduleRequest request) {
        logger.info("Serving request: {}", request);
        return service.schedule(request);
    }

    @GetMapping(CONFIGURATION)
    public List<ServiceConfig> getSupportedServices() {
        logger.info("Serving config request.");
        //        return service.getSupportedServices();
        // TODO: mock for testing multiple services, remove this
        var s = new LinkedList<>(service.getSupportedServices());
        s.addLast(new ServiceConfig(ScheduleType.DOCKER.toString(),
                List.of(
                        ParameterDescription.builder("dockerImageUrl")
                                .withTranslatedLabel(translationsProvider.t("services.docker.dynamicParams.dockerImageUrl.label"))
                                .ofText().build(),
                        ParameterDescription.builder("resultFilePath", false)
                                .withTranslatedLabel(translationsProvider.t("services.docker.dynamicParams.resultFilePath.label"))
                                .ofText().build()
                )));
        return s;
    }

    @GetMapping(CONFIGURATION + "/{scheduleType}")
    public ServiceConfig getServiceConfig(@PathVariable String scheduleType) {
        logger.info("Serving config request for {}", scheduleType);
        return service.getServiceConfig(ScheduleType.valueOf(scheduleType));
    }

    @PostMapping(AGGREGATED)
    public List<ScheduleResponse> schedule(@RequestBody Collection<ScheduleRequest> requests) {
        logger.info("Serving aggregated request: {}", requests);
        return requests.stream().map(service::schedule).toList();
    }

    @GetMapping("/test-request-to-server")
    public void testRequestToServer() throws URISyntaxException, ExecutionException, InterruptedException {
        // TODO remove this one day
        requestSender.sendGet(new URI(backendUrl + "/orchestrator-test"), new TypeReference<Void>() {
        }).get();
        System.out.println(backendRequestSender.getSignedDownloadUrl("1").get());
        System.out.println("DONE");
    }
}
