package com.swozo.api.web.servicemodule;

import com.swozo.api.orchestrator.OrchestratorService;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleReservationDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleSummaryDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleUsageDto;
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
import java.util.Map;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/service-modules")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class ServiceModuleController {
    private final Logger logger = LoggerFactory.getLogger(ServiceModuleController.class);
    private final ServiceModuleService service;
    private final OrchestratorService orchestratorService;

    @GetMapping("/all-system-modules")
    @PreAuthorize("hasRole('ADMIN')")
    public Collection<ServiceModuleSummaryDto> getModuleList(AccessToken token) {
        logger.info("serviceModule list");
        return service.getServiceModuleList();
    }

    @GetMapping("/summary")
    @PreAuthorize("hasRole('TEACHER')")
    public List<ServiceModuleSummaryDto> getAllPublicServiceModules() {
        return service.getAllPublicModules();
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public List<ServiceModuleSummaryDto> getUserModules(AccessToken accessToken) {
        return service.getModulesCreatedByTeacher(accessToken.getUserId());
    }

    @GetMapping("/user/summary")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public List<ServiceModuleSummaryDto> getUserModulesSummary(AccessToken accessToken) {
        return service.getModulesCreatedByTeacherSummary(accessToken.getUserId());
    }

    @GetMapping("/{serviceModuleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public ServiceModuleDetailsDto getServiceModule(AccessToken token, @PathVariable Long serviceModuleId) {
        logger.info("service serviceModule  info getter");
        return service.getServiceModuleInfo(serviceModuleId);
    }

    @GetMapping("/config")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public List<ServiceConfig> getSupportedServices() {
        return orchestratorService.getSupportedServices();
    }

    @PostMapping
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public ServiceModuleReservationDto initServiceModuleCreation(
            AccessToken token,
            @RequestBody ReserveServiceModuleRequest request
    ) {
        return service.initServiceModuleCreation(token.getUserId(), request);
    }

    @PutMapping
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public ServiceModuleDetailsDto finishServiceModuleCreation(
            AccessToken token,
            @RequestBody FinishServiceModuleCreationRequest request
    ) {
        return service.finishServiceModuleCreation(token.getUserId(), request);
    }

    @PutMapping("/{serviceModuleId}/edit/common")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public ServiceModuleDetailsDto updateCommonData(
            AccessToken token,
            @PathVariable Long serviceModuleId,
            @RequestBody ReserveServiceModuleRequest request
    ) {
        return service.updateCommonData(token.getUserId(), serviceModuleId, request);
    }

    @PutMapping("/{serviceModuleId}/edit/service-config/init")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public Map<String, Object> initServiceConfigUpdate(
            AccessToken token,
            @PathVariable Long serviceModuleId,
            @RequestBody ReserveServiceModuleRequest request
    ) {
         return service.initServiceConfigUpdate(token.getUserId(), serviceModuleId, request);
    }

    @PutMapping("/{serviceModuleId}/edit/service-config/finish")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public ServiceModuleDetailsDto finishServiceConfigUpdate(
            AccessToken token,
            @PathVariable Long serviceModuleId,
            @RequestBody FinishServiceModuleCreationRequest request
    ) {
        var txnData = service.finishServiceConfigUpdate(token.getUserId(), serviceModuleId, request);
        service.cleanupOldDataOutsideTxn(txnData);
        return txnData.serviceModule();
    }

    @GetMapping("/usage/{serviceModuleId}")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public List<ServiceModuleUsageDto> getUsage(
            AccessToken accessToken,
            @PathVariable Long serviceModuleId,
            @RequestParam(defaultValue = "0") Long offset,
            @RequestParam(defaultValue = "100") Long limit
    ) {
        return service.getServiceUsageInfo(serviceModuleId, accessToken.getUserId(), offset, limit);
    }

    @GetMapping("/{serviceModuleId}/edit")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public ReserveServiceModuleRequest getFormDataForEdit(AccessToken accessToken, @PathVariable Long serviceModuleId) {
        return service.getFormDataForEdit(serviceModuleId, accessToken.getUserId());
    }

    @DeleteMapping("/{serviceModuleId}")
    @PreAuthorize("hasRole('TECHNICAL_TEACHER')")
    public ServiceModuleDetailsDto deleteServiceModule(AccessToken accessToken, @PathVariable Long serviceModuleId) {
        var txnData = service.deleteServiceModule(accessToken.getUserId(), serviceModuleId);
        service.cleanupOldDataOutsideTxn(txnData);
        return txnData.serviceModule();
    }


//    @PutMapping("/{moduleId}")
//    @PreAuthorize("hasRole('TACHER')")
//    public ServiceModuleDetailsResp updateServiceModule(AccessToken token, @PathVariable Long moduleId, @RequestBody ServiceModule newServiceModule) {
//        System.out.println("updating serviceModule with id: " + moduleId);
//        return new ServiceModule();
//    }
}
