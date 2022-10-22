package com.swozo.api.web.activity;

import com.swozo.api.common.files.FileService;
import com.swozo.api.common.files.dto.UploadAccessDto;
import com.swozo.api.common.files.request.InitFileUploadRequest;
import com.swozo.api.common.files.storage.FilePathProvider;
import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.exceptions.types.course.ActivityNotFoundException;
import com.swozo.api.web.exceptions.types.files.FileNotFoundException;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.ActivityMapper;
import com.swozo.model.utils.StorageAccessRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityValidator activityValidator;
    private final FileService fileService;
    private final FilePathProvider filePathProvider;
    private final ActivityMapper activityMapper;
    private final UserService userService;


    public StorageAccessRequest preparePublicActivityFileUpload(
            Long activityId,
            Long teacherId,
            InitFileUploadRequest initFileUploadRequest
    ) {
        var activity = activityRepository.findById(activityId).orElseThrow();

        return fileService.prepareExternalUpload(
                initFileUploadRequest,
                filePathProvider.publicActivityFilePath(activity),
                () -> activityValidator.validateAddActivityFileRequest(activity, teacherId, initFileUploadRequest)
        );
    }

    public ActivityDetailsDto ackPublicActivityFileUpload(
            Long activityId,
            Long uploaderId,
            UploadAccessDto uploadAccessDto
    ) {
        return activityMapper.toDto(
                fileService.acknowledgeExternalUpload(
                    userService.getUserById(uploaderId),
                    uploadAccessDto,
                    () -> activityRepository.findById(activityId).orElseThrow(),
                    (file, activity) -> {
                        activity.addPublicFile(file);
                        return activityRepository.save(activity);
                    }
                )
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


    // TODO: below are old, currently unused and left 'just in case' things

//    public Activity createActivity(Activity newActivity) {
//        newActivity.getModules().forEach(activityModule -> activityModule.setActivity(newActivity));
//        var courseID = newActivity.getCourse().getId();
//        var course = courseRepository.getById(courseID);
//        course.addActivity(newActivity);
//        newActivity.setCourse(course);
//        activityRepository.save(newActivity);
//        return newActivity;
//    }
//
//    public void deleteActivity(Long activityId) {
//        var activity = activityRepository.getById(activityId);
//        var course = courseRepository.getById(activity.getCourse().getId());
//        course.deleteActivity(activity);
//        activityRepository.deleteById(activityId);
//    }
//
//    public Activity updateActivity(Long id, Activity newActivity) {
//        var activity = activityRepository.getById(id);
//        activity.setName(newActivity.getName());
//        activity.setDescription(newActivity.getDescription());
//        activity.setStartTime(newActivity.getStartTime());
//        activity.setEndTime(newActivity.getEndTime());
//        activity.setInstructionsFromTeacher(newActivity.getInstructionsFromTeacher());
//        activityRepository.save(activity);
//        return activity;
//    }
//
//    public Collection<ActivityModule> getActivityModulesList(Long activityId) {
//        return activityRepository.getById(activityId).getModules();
//    }
//
//    public Activity addModuleToActivity(Long activityId, Long activityModuleId) {
//        var activity = activityRepository.getById(activityId);
//        var activityModule = activityModuleRepository.getById(activityModuleId);
//        activity.addActivityModule(activityModule);
//        activityRepository.save(activity);
//        return activity;
//    }
//
//    public Activity deleteModuleFromActivity(Long activityId, Long activityModuleId) {
//        var activity = activityRepository.getById(activityId);
//        var activityModule = activityModuleRepository.getById(activityModuleId);
//        activity.removeActivityModule(activityModule);
//        activityRepository.save(activity);
//        return activity;
//    }

}
