package com.swozo.api.web;

import com.swozo.databasemodel.Activity;
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
@RequestMapping("/activities")
@SecurityRequirement(name = ACCESS_TOKEN)
public class ActivityController {
    private final String activityService = "course service";

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Activity getActivity(AccessToken token, @PathVariable long id){
        System.out.println("activity  info getter");
        return new Activity();
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public String addActivity(AccessToken token, @RequestBody Activity activity) {
        System.out.println("creating new activity inside course");
        return "activity_id";
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteActivity(AccessToken token, @PathVariable long id) {
        System.out.println("deleting activity from course");
        return "activity deleted";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TACHER')")
    public String updateActivity(AccessToken token, @PathVariable long id, @RequestBody Activity newActivity){
        System.out.println("updating activity from course");
        return "activity updated";
    }

    @GetMapping("/{id}/service-modules")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<ServiceModule> getCourseActivityList(AccessToken token, @PathVariable long id) {
        System.out.println("service module list from activity with id: " + id);
        return new LinkedList<>();
    }

    @PostMapping("/{activityId}/service-modules/{moduleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String addModuleToActivity(AccessToken token, @PathVariable long activityId, @PathVariable long moduleId){
        System.out.println("adding module with id: " + moduleId + " to activity with id: " + activityId);
        return "module added";
    }

    @DeleteMapping("/{activityId}/service-modules/{moduleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteModuleFromActivity(AccessToken token, @PathVariable long activityId, @PathVariable long moduleId){
        System.out.println("adding module with id: " + moduleId + " to activity with id: " + activityId);
        return "service module deleted";
    }

    @GetMapping("/{id}/links")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public Collection<String> getLinks(AccessToken token, @PathVariable long id){
        System.out.println("sending links");
        return new LinkedList<>();
    }

}