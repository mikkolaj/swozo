package com.swozo.api.web.activity;

import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.activity.dto.ActivityFilesDto;
import com.swozo.api.web.activity.dto.ActivitySummaryDto;
import com.swozo.api.web.activity.dto.TeacherActivityFilesDto;
import com.swozo.api.web.activitymodule.ActivityModuleService;
import com.swozo.api.web.auth.AuthService;
import com.swozo.model.files.InitFileUploadRequest;
import com.swozo.model.files.StorageAccessRequest;
import com.swozo.model.files.UploadAccessDto;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.users.OrchestratorUserDto;
import com.swozo.security.AccessToken;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @GetMapping
    public List<ActivitySummaryDto> getUserActivities(
            AccessToken accessToken,
            @RequestParam(defaultValue = "31") Integer daysInThePast,
            @RequestParam(defaultValue = "31") Integer daysInTheFuture
    ) {
        return activityService.getUserActivitiesBetween(
                accessToken.getUserId(),
                LocalDateTime.now().minusDays(daysInThePast),
                LocalDateTime.now().plusDays(daysInTheFuture)
        );
    }

    @GetMapping("/{activityId}/files/results/student")
    public ActivityFilesDto getActivityResultFilesForUser(AccessToken accessToken, @PathVariable Long activityId) {
        return activityService.getUserActivityFiles(accessToken.getUserId(), activityId);
    }

    @GetMapping("/{activityId}/files/results/teacher")
    @PreAuthorize("hasRole('TEACHER')")
    public TeacherActivityFilesDto getActivityResultFilesForAllStudents(AccessToken accessToken, @PathVariable Long activityId) {
        return activityService.getActivityResultFilesForAllStudents(accessToken.getUserId(), activityId);
    }

    @PostMapping("/{activityId}/cancel")
    @PreAuthorize("hasRole('TEACHER')")
    public ActivityDetailsDto cancelActivity(AccessToken accessToken, @PathVariable Long activityId) {
        return activityService.cancelActivity(accessToken.getUserId(), activityId);
    }

    @PostMapping("/{activityId}/files/public")
    @PreAuthorize("hasRole('TEACHER')")
    public StorageAccessRequest preparePublicActivityFileUpload(
            AccessToken token,
            @PathVariable Long activityId,
            @RequestBody InitFileUploadRequest initFileUploadRequest
    ) {
        return activityService.preparePublicActivityFileUpload(activityId, token.getUserId(), initFileUploadRequest);
    }

    @PutMapping("/{activityId}/files/public")
    @PreAuthorize("hasRole('TEACHER')")
    public ActivityDetailsDto ackPublicActivityFileUpload(
            AccessToken accessToken,
            @PathVariable Long activityId,
            @RequestBody UploadAccessDto uploadAccessDto
    ) {
        return activityService.ackPublicActivityFileUpload(activityId, accessToken.getUserId(), uploadAccessDto);
    }

    @GetMapping("/{activityId}/files/public/download/{fileId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public StorageAccessRequest getPublicActivityFileDownloadRequest(
            AccessToken accessToken,
            @PathVariable Long activityId,
            @PathVariable Long fileId
    ) {
        return activityService.getPublicActivityFileDownloadRequest(accessToken.getUserId(), activityId, fileId);
    }

    @GetMapping("/{activityId}/files/results/download/{fileId}")
    @PreAuthorize("hasAnyRole('STUDENT', 'TEACHER')")
    public StorageAccessRequest getActivityResultFileDownloadRequest(
            AccessToken accessToken,
            @PathVariable Long activityId,
            @PathVariable Long fileId
    ) {
        return activityService.getActivityResultFileDownloadRequest(accessToken.getUserId(), activityId, fileId);
    }

    @PutMapping("/confirm-link-delivery/{activityModuleId}")
    @PreAuthorize("hasRole('TEACHER')")
    public void confirmLinkCanBeDeliveredToStudents(
            AccessToken accessToken,
            @PathVariable Long activityModuleId
    ) {
       activityModuleService.confirmLinkCanBeDeliveredToStudents(accessToken.getUserId(), activityModuleId);
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

    @PostMapping(INTERNAL + INIT_UPLOAD + "/{activityModuleId}/{userId}")
    public StorageAccessRequest initUserActivityFileUpload(
            @PathVariable Long activityModuleId,
            @PathVariable Long userId,
            @RequestBody InitFileUploadRequest initFileUploadRequest
    ) {
        return activityModuleService.prepareUserActivityFileUpload(initFileUploadRequest, activityModuleId, userId);
    }

    @PutMapping(INTERNAL + ACK_UPLOAD + "/{activityModuleId}/{scheduleRequestId}/{userId}")
    public void ackUserActivityFileUpload(
            @PathVariable Long activityModuleId,
            @PathVariable Long scheduleRequestId,
            @PathVariable Long userId,
            @RequestBody UploadAccessDto uploadAccessDto
    ) {
        activityModuleService.ackUserActivityFileUpload(uploadAccessDto, activityModuleId, scheduleRequestId, userId);
    }
}
