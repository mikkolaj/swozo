package com.swozo.api.web.activity;

import com.swozo.api.common.files.FileService;
import com.swozo.model.files.UploadAccessDto;
import com.swozo.model.files.InitFileUploadRequest;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.activity.dto.ActivitySummaryDto;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.CourseValidator;
import com.swozo.api.web.exceptions.types.course.ActivityNotFoundException;
import com.swozo.api.web.exceptions.types.files.FileNotFoundException;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.ActivityMapper;
import com.swozo.model.files.StorageAccessRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityValidator activityValidator;
    private final CourseValidator courseValidator;
    private final FileService fileService;
    private final FilePathProvider filePathProvider;
    private final ActivityMapper activityMapper;
    private final UserService userService;

    public List<ActivitySummaryDto> getUserActivitiesBetween(Long userId, LocalDateTime start, LocalDateTime end) {
        return activityRepository.getAllUserActivitiesBetween(userId, start, end).stream()
                .map(activityMapper::toSummaryDto)
                .toList();
    }

    public StorageAccessRequest preparePublicActivityFileUpload(
        Long activityId,
        Long teacherId,
        InitFileUploadRequest initFileUploadRequest
    ) {
        var activity = activityRepository.findById(activityId).orElseThrow();

        return fileService.prepareExternalUpload(
                initFileUploadRequest,
                filePathProvider.publicActivityFilePath(activity),
                () -> {
                    courseValidator.validateCreatorAndNotSandbox(activity.getCourse(), teacherId);
                    activityValidator.validateAddActivityFileRequest(activity, teacherId, initFileUploadRequest);
                }
        );
    }

    public ActivityDetailsDto ackPublicActivityFileUpload(
            Long activityId,
            Long uploaderId,
            UploadAccessDto uploadAccessDto
    ) {
        var user = userService.getUserById(uploaderId);
        return activityMapper.toDto(
                fileService.acknowledgeExternalUpload(
                    user,
                    uploadAccessDto,
                    () -> activityRepository.findById(activityId).orElseThrow(),
                    (file, activity) -> {
                        activity.addPublicFile(file);
                        return activityRepository.save(activity);
                    }
                ),
                user
        );
    }

    public StorageAccessRequest getPublicActivityFileDownloadRequest(
            Long userId,
            Long activityId,
            Long fileId,
            RoleDto userRole
    ) {
        var activity = activityRepository.findById(activityId).orElseThrow(() -> ActivityNotFoundException.withId(activityId));
        var file = activity.getPublicFiles().stream().filter(f -> f.getId().equals(fileId))
                .findAny().orElseThrow(() -> FileNotFoundException.inContext("public activity files", fileId));

        activityValidator.validateDownloadPublicActivityFileRequest(userId, activity, userRole);

        return fileService.createExternalDownloadRequest(file);
    }
}
