package com.swozo.webservice.controller;

import com.swozo.databasemodel.Activity;
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
    public ServiceModule getServiceModule(AccessToken token){
        System.out.println("service module  info getter");
        return new ServiceModule();
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public String addModuleToActivity(AccessToken token){
        System.out.println("adding module to activity");
        return "activity_id";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteModuleFromActivity(AccessToken token){
        System.out.println("deleting module from activity");
        return "service module deleted";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TACHER')")
    public String updateServiceModule(@PathVariable int id, @RequestBody ServiceModule newServiceModule){
        System.out.println("updating activity from course");
        return "activity updated";
    }
}
