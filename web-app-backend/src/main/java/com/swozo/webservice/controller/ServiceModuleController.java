package com.swozo.webservice.controller;

import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.ServiceModule;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/service_module")
@SecurityRequirement(name = ACCESS_TOKEN)
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ServiceModuleController {

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public ServiceModule getServiceModule(AccessToken token, @PathVariable long id){
        System.out.println("service module  info getter");
        return new ServiceModule();
    }

    @PostMapping("/{activityId}/{moduleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String addModuleToActivity(AccessToken token, @PathVariable long activityId, @PathVariable long moduleId){
        System.out.println("adding module with id: " + moduleId + " to activity with id: " + activityId);
        return "module added";
    }

    @DeleteMapping("/{activityId}/{moduleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteModuleFromActivity(AccessToken token, @PathVariable long activityId, @PathVariable long moduleId){
        System.out.println("adding module with id: " + moduleId + " to activity with id: " + activityId);;
        return "service module deleted";
    }

    @PutMapping("/{moduleId}")
    @PreAuthorize("hasRole('TACHER')")
    public String updateServiceModule(AccessToken token, @PathVariable long moduleId, @RequestBody ServiceModule newServiceModule){
        System.out.println("updating module with id: " + moduleId);
        return "module updated";
    }
}
