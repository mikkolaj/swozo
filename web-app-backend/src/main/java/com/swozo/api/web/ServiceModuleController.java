package com.swozo.api.web;

import com.swozo.databasemodel.ServiceModule;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.LinkedList;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/service-modules")
@SecurityRequirement(name = ACCESS_TOKEN)
public class ServiceModuleController {

    @GetMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<ServiceModule> getModuleList(AccessToken token){
        System.out.println("module list");
        return new LinkedList<>();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ServiceModule getServiceModule(AccessToken token, @PathVariable long id){
        System.out.println("service module  info getter");
        return new ServiceModule();
    }

    @PutMapping("/{moduleId}")
    @PreAuthorize("hasRole('TACHER')")
    public String updateServiceModule(AccessToken token, @PathVariable long moduleId, @RequestBody ServiceModule newServiceModule){
        System.out.println("updating module with id: " + moduleId);
        return "module updated";
    }
}