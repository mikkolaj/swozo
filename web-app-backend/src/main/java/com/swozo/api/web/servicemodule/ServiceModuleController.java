package com.swozo.api.web.servicemodule;

import com.swozo.api.orchestrator.OrchestratorService;
import com.swozo.api.web.servicemodule.dto.ServiceConfigDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.model.scheduling.ParameterDescription;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.security.AccessToken;
import com.swozo.utils.SupportedLanguage;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/service-modules")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class ServiceModuleController {
    private final Logger logger = LoggerFactory.getLogger(ServiceModuleController.class);
    private final ServiceModuleService serviceModuleService;
    private final OrchestratorService orchestratorService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<ServiceModuleDetailsDto> getModuleList(AccessToken token) {
        logger.info("module list");
        return serviceModuleService.getServiceModuleList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ServiceModuleDetailsDto getServiceModule(AccessToken token, @PathVariable Long id) {
        logger.info("service module  info getter");
        return serviceModuleService.getServiceModuleInfo(id);
    }

    @GetMapping("/config")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public List<ServiceConfigDto> getSupportedServices() {
//        return orchestratorService.getSupportedServices();
        var s = new LinkedList<>(orchestratorService.getSupportedServices());
        s.push(new ServiceConfigDto(ScheduleType.DOCKER, "DOCKER",
                List.of(
                        ParameterDescription.builder("test1")
                                .withTranslatedLabel(Map.of(SupportedLanguage.PL, "label pl"))
                                .ofText().build(),
                        ParameterDescription.builder("test2")
                                .withTranslatedLabel(Map.of(SupportedLanguage.PL, "label pl2"))
                                .ofFile().build()
                )));
        return s;
    }

//    @PutMapping("/{moduleId}")
//    @PreAuthorize("hasRole('TACHER')")
//    public ServiceModuleDetailsResp updateServiceModule(AccessToken token, @PathVariable Long moduleId, @RequestBody ServiceModule newServiceModule) {
//        System.out.println("updating module with id: " + moduleId);
//        return new ServiceModule();
//    }
}
