package com.swozo.api.web.activity;

import com.swozo.api.common.files.dto.UploadAccessDto;
import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.activitymodule.ActivityModuleService;
import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.utils.StorageAccessRequest;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping("/activities")
@SecurityRequirement(name = ACCESS_TOKEN)
@RequiredArgsConstructor
public class ActivityController {
    private final Logger logger = LoggerFactory.getLogger(ActivityController.class);
    private final ActivityService activityService;
    private final ActivityModuleService activityModuleService;
    private final AuthService authService;


    @PostMapping("/{activityId}/files")
    @PreAuthorize("hasRole('TEACHER')")
    public StorageAccessRequest preparePublicActivityFileUpload(
            AccessToken token,
            @PathVariable Long activityId,
            @RequestBody InitFileUploadRequest initFileUploadRequest
    ) {
        return activityService.preparePublicActivityFileUpload(activityId, token.getUserId(), initFileUploadRequest);
    }

    @PutMapping("/{activityId}/files")
    @PreAuthorize("hasRole('TEACHER')")
    public ActivityDetailsDto ackPublicActivityFileUpload(
            AccessToken accessToken,
            @PathVariable Long activityId,
            @RequestBody UploadAccessDto uploadAccessDto
    ) {
        return activityService.ackPublicActivityFileUpload(activityId, accessToken.getUserId(), uploadAccessDto);
    }
    
    @GetMapping("/{activityId}/files/{fileId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public StorageAccessRequest getPublicActivityFileDownloadRequest(
            AccessToken accessToken,
            @PathVariable Long activityId,
            @PathVariable Long fileId
    ) {
        var role = authService.oneOf(accessToken, RoleDto.STUDENT, RoleDto.TEACHER);
        return activityService.getPublicActivityFileDownloadRequest(accessToken.getUserId(), activityId, fileId, role);
    }

    @PutMapping("/internal/links/{requestId}")
    public void setActivityLinks(
            @PathVariable Long requestId,
            @RequestBody List<ActivityLinkInfo> links
    ) {
        activityModuleService.setActivityLinks(requestId, links);
    }
    


    // TODO return proper DTO types (based on frontend requirements) instead of persistence types
//    //wouldn't it be better to pass course_id via path?
//    @PostMapping()
//    @PreAuthorize("hasRole('TEACHER')")
//    public Activity addActivity(AccessToken token, @RequestBody Activity activity) {
//        logger.info("creating new activity with name {} by user {}", activity.getName(), token.getUserId());
//        return activityService.createActivity(activity);
//    }
//
//    @DeleteMapping("/{id}")
//    @PreAuthorize("hasRole('TEACHER')")
//    public void deleteActivity(AccessToken token, @PathVariable Long id) {
//        logger.info("deleting activity with id: {}", id);
//        activityService.deleteActivity(id);
//    }
//
//    @PutMapping("/{id}")
//    @PreAuthorize("hasRole('TEACHER')")
//    public Activity updateActivity(AccessToken token, @PathVariable Long id, @RequestBody Activity newActivity) {
//        logger.info("updating activity with name: {}", newActivity.getName());
//        return activityService.updateActivity(id, newActivity);
//    }
//
//    @GetMapping("/{id}/service-modules")
//    @PreAuthorize("hasRole('TEACHER')")
//    public Collection<ActivityModule> getCourseActivityList(AccessToken token, @PathVariable Long id) {
//        logger.info("service serviceModule list from activity with id: {}", id);
//        return activityService.getActivityModulesList(id);
//    }
//
//    @PostMapping("/{activityId}/service-modules/{activityModuleId}")
//    @PreAuthorize("hasRole('TEACHER')")
//    public Activity addModuleToActivity(AccessToken token, @PathVariable Long activityId, @PathVariable Long activityModuleId) {
//        logger.info("adding serviceModule with id: {} to activity with id: {}", activityModuleId, activityId);
//        return activityService.addModuleToActivity(activityId, activityModuleId);
//    }
//
//    @DeleteMapping("/{activityId}/service-modules/{activityModuleId}")
//    @PreAuthorize("hasRole('TEACHER')")
//    public Activity deleteModuleFromActivity(AccessToken token, @PathVariable Long activityId, @PathVariable Long activityModuleId) {
//        logger.info("removing serviceModule with id: {} from activity with id: {}", activityModuleId, activityId);
//        return activityService.deleteModuleFromActivity(activityId, activityModuleId);
//    }
//
//
//    //imo this endpoint should be removed, connectionDetails are now stored in the ActivityModule, and we already have a method for getting them
//    @GetMapping("/{id}/links")
//    @PreAuthorize("hasAnyRole('TEACHER', 'STUDENT')")
//    public Collection<String> getLinks(AccessToken token, @PathVariable Long id) {
//        logger.info("sending connectionDetails");
//        return new LinkedList<>();
//    }

}
