package com.swozo.api.web.activity;

import com.swozo.api.common.files.FileRepository;
import com.swozo.api.common.files.FileService;
import com.swozo.api.common.files.dto.FileDto;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.activity.dto.ActivityFilesDto;
import com.swozo.api.web.activity.dto.ActivitySummaryDto;
import com.swozo.api.web.activity.dto.TeacherActivityFilesDto;
import com.swozo.api.web.course.CourseValidator;
import com.swozo.api.web.exceptions.types.course.ActivityNotFoundException;
import com.swozo.api.web.exceptions.types.files.FileNotFoundException;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.ActivityMapper;
import com.swozo.mapper.FileMapper;
import com.swozo.mapper.UserMapper;
import com.swozo.model.files.InitFileUploadRequest;
import com.swozo.model.files.StorageAccessRequest;
import com.swozo.model.files.UploadAccessDto;
import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.UserActivityModuleInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final UserMapper userMapper;
    private final FileMapper fileMapper;
    private final FileRepository fileRepository;

    public List<ActivitySummaryDto> getUserActivitiesBetween(Long userId, LocalDateTime start, LocalDateTime end) {
        return activityRepository.getAllUserActivitiesBetween(userId, start, end).stream()
                .map(activityMapper::toSummaryDto)
                .toList();
    }

    public ActivityFilesDto getUserActivityFiles(Long userId, Long activityId) {
        var activity = activityRepository.findById(activityId).orElseThrow();
        activityValidator.validateIsParticipant(userId, activity);
        return collectUserActivityFiles(userId, activity);
    }

    public TeacherActivityFilesDto getActivityResultFilesForAllStudents(Long teacherId, Long activityId) {
        var activity = activityRepository.findById(activityId).orElseThrow();
        activityValidator.validateIsTeacher(teacherId, activity);

        return new TeacherActivityFilesDto(
                activity.getCourse().getStudentsAsUsers().stream()
                    .collect(Collectors.toMap(
                            BaseEntity::getId,
                            user -> collectUserActivityFiles(user.getId(), activity)
                    )),
                activity.getCourse().getStudentsAsUsers().stream()
                    .collect(Collectors.toMap(
                            BaseEntity::getId,
                            userMapper::toDto
                    ))
        );
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
            Long fileId
    ) {
        var activity = activityRepository.findById(activityId).orElseThrow(() -> ActivityNotFoundException.withId(activityId));
        var file = activity.getPublicFiles().stream().filter(f -> f.getId().equals(fileId))
                .findAny().orElseThrow(() -> FileNotFoundException.inContext("public activity files", fileId));

        activityValidator.validateDownloadPublicActivityFileRequest(userId, activity);

        return fileService.createExternalDownloadRequest(file);
    }

    public StorageAccessRequest getActivityResultFileDownloadRequest(
            Long userId,
            Long activityId,
            Long fileId
    ) {
        var activity = activityRepository.findById(activityId).orElseThrow();
        var file = fileRepository.findById(fileId).orElseThrow();
        activityValidator.validateDownloadActivityResultFileRequest(userId, activity, file);

        return fileService.createExternalDownloadRequest(file);
    }

    private ActivityFilesDto collectUserActivityFiles(Long userId, Activity activity) {
        var activityModuleIdToUserFiles = new HashMap<Long, List<FileDto>>();

        for (var activityModule : activity.getModules()) {
            var activityModuleUserFiles = activityModule.getSchedules().stream()
                    .flatMap(scheduleInfo -> scheduleInfo.getUserActivityModuleData().stream())
                    .filter(userActivityModuleInfo -> userActivityModuleInfo.getUser().getId().equals(userId))
                    .map(UserActivityModuleInfo::getFile)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .map(fileMapper::toDto)
                    .toList();

            activityModuleIdToUserFiles.put(activityModule.getId(), activityModuleUserFiles);
        }

        return new ActivityFilesDto(activityModuleIdToUserFiles);
    }
}
