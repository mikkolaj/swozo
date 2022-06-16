package com.swozo.api.web;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.ActivityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.LinkedList;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/activities")
@SecurityRequirement(name = ACCESS_TOKEN)
public class ActivityController {
    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Activity getActivity(AccessToken token, @PathVariable Long id) {
        System.out.println("activity  info getter");
        return activityService.getActivity(id);
    }

    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Activity addActivity(AccessToken token, @RequestBody Activity activity) {
        System.out.println("creating new activity inside course");
        return activityService.createActivity(activity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public String deleteActivity(AccessToken token, @PathVariable Long id) {
        System.out.println("deleting activity from course");
        activityService.deleteActivity(id);
        return "activity deleted";
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TACHER')")
    public Activity updateActivity(AccessToken token, @PathVariable Long id, @RequestBody Activity newActivity) {
        System.out.println("updating activity from course");
        return activityService.updateActivity(id, newActivity);
    }

    @GetMapping("/{id}/service-modules")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<ActivityModule> getCourseActivityList(AccessToken token, @PathVariable Long id) {
        System.out.println("service module list from activity with id: " + id);
        return activityService.activityModulesList(id);
    }

    @PostMapping("/{activityId}/service-modules/{moduleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Activity addModuleToActivity(AccessToken token, @PathVariable Long activityId, @PathVariable Long moduleId) {
        System.out.println("adding module with id: " + moduleId + " to activity with id: " + activityId);
        return activityService.addModuleToActivity(activityId, moduleId);
    }

    @DeleteMapping("/{activityId}/service-modules/{moduleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Activity deleteModuleFromActivity(AccessToken token, @PathVariable Long activityId, @PathVariable Long moduleId) {
        System.out.println("adding module with id: " + moduleId + " to activity with id: " + activityId);
        return activityService.deleteModuleFromActivity(activityId, moduleId);
    }

    @GetMapping("/{id}/links")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public Collection<String> getLinks(AccessToken token, @PathVariable Long id) {
        System.out.println("sending links");
        return new LinkedList<>();
    }

}
