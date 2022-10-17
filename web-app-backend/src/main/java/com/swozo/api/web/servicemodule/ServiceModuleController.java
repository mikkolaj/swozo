package com.swozo.api.web.servicemodule;

import com.swozo.api.orchestrator.OrchestratorService;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleReservationDto;
import com.swozo.api.web.servicemodule.request.FinishServiceModuleCreationRequest;
import com.swozo.api.web.servicemodule.request.ReserveServiceModuleRequest;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/service-modules")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class ServiceModuleController {
    private final Logger logger = LoggerFactory.getLogger(ServiceModuleController.class);
    private final ServiceModuleService service;
    private final OrchestratorService orchestratorService;

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<ServiceModuleDetailsDto> getModuleList(AccessToken token) {
        logger.info("module list");
        return service.getServiceModuleList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ServiceModuleDetailsDto getServiceModule(AccessToken token, @PathVariable Long id) {
        logger.info("service module  info getter");
        return service.getServiceModuleInfo(id);
    }

    @GetMapping("/config")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public List<ServiceConfig> getSupportedServices() {
        return orchestratorService.getSupportedServices();
    }

    @PostMapping
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public ServiceModuleReservationDto reserveServiceModuleCreation(
            AccessToken token,
            @RequestBody ReserveServiceModuleRequest request
    ) {
        return service.reserveServiceModuleCreation(token.getUserId(), request);
    }

    @PutMapping
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public ServiceModuleDetailsDto finishServiceModuleCreation(
            AccessToken token,
            @RequestBody FinishServiceModuleCreationRequest request
    ) {
        return service.finishServiceModuleCreation(token.getUserId(), request);
    }


//    @PutMapping("/{moduleId}")
//    @PreAuthorize("hasRole('TACHER')")
//    public ServiceModuleDetailsResp updateServiceModule(AccessToken token, @PathVariable Long moduleId, @RequestBody ServiceModule newServiceModule) {
//        System.out.println("updating module with id: " + moduleId);
//        return new ServiceModule();
//    }
}
