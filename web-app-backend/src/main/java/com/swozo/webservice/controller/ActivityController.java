package com.swozo.webservice.controller;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
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
@RequestMapping("/activity")
@SecurityRequirement(name = ACCESS_TOKEN)
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ActivityController {
    private final String activityService = "course service";

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Course getActivity(AccessToken token){
        System.out.println("activity  info getter");
        return new Course();
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public String addActivity(AccessToken token, @RequestBody String params) {
        System.out.println("creating new activity inside course");
        return "activity_id";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteActivity(AccessToken token) {
        System.out.println("deleting activity from course");
        return "activity deleted";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TACHER')")
    public String updateActivity(@PathVariable int id, @RequestBody Activity newActivity){
        System.out.println("updating activity from course");
        return "activity updated";
    }

    @GetMapping("/module_list")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<ServiceModule> getModuleList(AccessToken token){
        System.out.println("module list");
        return new LinkedList<>();
    }

//    pytanie czy jaką tu dać autroyzacje? bo czy jak więcej ról moze mieć
//    dostęp to mam wpisywać więcej PreAuthorize?
    @GetMapping("/links")
//    @PreAuthorize("hasRole('TEACHER')")
    public String getLinks(AccessToken token){
        System.out.println("sending links");
        return "links";
    }

}
