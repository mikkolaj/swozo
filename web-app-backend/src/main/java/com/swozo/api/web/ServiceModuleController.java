package com.swozo.api.web;

import com.swozo.dto.servicemodule.ServiceModuleDetailsDto;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.ServiceModuleService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/service-modules")
@SecurityRequirement(name = ACCESS_TOKEN)
public class ServiceModuleController {
    private final ServiceModuleService serviceModuleService;

    @Autowired
    public ServiceModuleController(ServiceModuleService serviceModuleService) {
        this.serviceModuleService = serviceModuleService;
    }

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<ServiceModuleDetailsDto> getModuleList(AccessToken token) {
        System.out.println("module list");
        return serviceModuleService.getServiceModuleList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ServiceModuleDetailsDto getServiceModule(AccessToken token, @PathVariable Long id) {
        System.out.println("service module  info getter");
        return serviceModuleService.getServiceModuleInfo(id);
    }

//    Na razie nie dajemy możliwosci dodawania mdoułów, jest 1 i tyle
//    @PutMapping("/{moduleId}")
//    @PreAuthorize("hasRole('TACHER')")
//    public ServiceModuleDetailsResp updateServiceModule(AccessToken token, @PathVariable Long moduleId, @RequestBody ServiceModule newServiceModule) {
//        System.out.println("updating module with id: " + moduleId);
//        return new ServiceModule();
//    }
}
