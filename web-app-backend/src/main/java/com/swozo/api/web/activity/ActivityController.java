package com.swozo.api.web.activity;

import com.swozo.api.common.files.dto.UploadAccessDto;
import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.activitymodule.ActivityModuleService;
import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.model.utils.StorageAccessRequest;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.swozo.config.Config.*;
import static com.swozo.config.SwaggerConfig.ACCESS_TOKEN;

@RestController
@RequestMapping(ACTIVITIES)
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

    @PutMapping(INTERNAL + LINKS + "/{activityModuleId}/{scheduleRequestId}")
    public void setActivityLinks(
            @PathVariable Long activityModuleId,
            @PathVariable Long scheduleRequestId,
            @RequestBody List<ActivityLinkInfo> links
    ) {
        activityModuleService.addActivityLinks(activityModuleId, scheduleRequestId, links);
    }

    @GetMapping(INTERNAL + USERS + "/{activityModuleId}/{scheduleRequestId}")
    public List<OrchestratorUserDto> getUserDataForProvisioner(
            @PathVariable Long activityModuleId,
            @PathVariable Long scheduleRequestId
    ) {
        return activityModuleService.getUserDataForProvisioner(activityModuleId, scheduleRequestId);
    }
}
