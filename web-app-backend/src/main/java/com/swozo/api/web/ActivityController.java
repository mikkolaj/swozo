package com.swozo.api.web;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.security.AccessToken;
import com.swozo.webservice.service.ActivityService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    private final ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Activity getActivity(AccessToken token, @PathVariable Long id) {
        logger.info("activity info for id {}", id);
        return activityService.getActivity(id);
    }

    //wouldn't it be better to pass course_id via path?
    @PostMapping()
    @PreAuthorize("hasRole('TEACHER')")
    public Activity addActivity(AccessToken token, @RequestBody Activity activity) {
        logger.info("creating new activity with name {} by user {}", activity.getName(), token.getUserId());
        return activityService.createActivity(activity);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public void deleteActivity(AccessToken token, @PathVariable Long id) {
        logger.info("deleting activity with id: {}", id);
        activityService.deleteActivity(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('TEACHER')")
    public Activity updateActivity(AccessToken token, @PathVariable Long id, @RequestBody Activity newActivity) {
        logger.info("updating activity with name: {}", newActivity.getName());
        return activityService.updateActivity(id, newActivity);
    }

    @GetMapping("/{id}/service-modules")
    @PreAuthorize("hasRole('TEACHER')")
    public Collection<ActivityModule> getCourseActivityList(AccessToken token, @PathVariable Long id) {
        logger.info("service module list from activity with id: {}", id);
        return activityService.activityModulesList(id);
    }

    @PostMapping("/{activityId}/service-modules/{activityModuleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Activity addModuleToActivity(AccessToken token, @PathVariable Long activityId, @PathVariable Long activityModuleId) {
        logger.info("adding module with id: {} to activity with id: {}", activityModuleId, activityId);
        return activityService.addModuleToActivity(activityId, activityModuleId);
    }

    @DeleteMapping("/{activityId}/service-modules/{activityModuleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public Activity deleteModuleFromActivity(AccessToken token, @PathVariable Long activityId, @PathVariable Long activityModuleId) {
        logger.info("removing module with id: {} from activity with id: {}", activityModuleId, activityId);
        return activityService.deleteModuleFromActivity(activityId, activityModuleId);
    }


    //imo this endpoint should be removed, links are now stored in the ActivityModule, and we already have a method for getting them
    @GetMapping("/{id}/links")
    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
    public Collection<String> getLinks(AccessToken token, @PathVariable Long id) {
        logger.info("sending links");
        return new LinkedList<>();
    }

}
