package com.swozo.api.web.activity;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.course.CourseRepository;
import com.swozo.persistence.Activity;
import com.swozo.persistence.ActivityModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ActivityService {
    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
    private final ActivityModuleRepository activityModuleRepository;

    public Activity createActivity(Activity newActivity) {
        newActivity.getModules().forEach(activityModule -> activityModule.setActivity(newActivity));
        var courseID = newActivity.getCourse().getId();
        var course = courseRepository.getById(courseID);
        course.addActivity(newActivity);
        newActivity.setCourse(course);
        activityRepository.save(newActivity);
        return newActivity;
    }

    public void deleteActivity(Long activityId) {
        var activity = activityRepository.getById(activityId);
        var course = courseRepository.getById(activity.getCourse().getId());
        course.deleteActivity(activity);
        activityRepository.deleteById(activityId);
    }

    public Activity updateActivity(Long id, Activity newActivity) {
        var activity = activityRepository.getById(id);
        activity.setName(newActivity.getName());
        activity.setDescription(newActivity.getDescription());
        activity.setStartTime(newActivity.getStartTime());
        activity.setEndTime(newActivity.getEndTime());
        activity.setInstructionsFromTeacher(newActivity.getInstructionsFromTeacher());
        activityRepository.save(activity);
        return activity;
    }

    public Collection<ActivityModule> getActivityModulesList(Long activityId) {
        return activityRepository.getById(activityId).getModules();
    }

    public Activity addModuleToActivity(Long activityId, Long activityModuleId) {
        var activity = activityRepository.getById(activityId);
        var activityModule = activityModuleRepository.getById(activityModuleId);
        activity.addActivityModule(activityModule);
        activityRepository.save(activity);
        return activity;
    }

    public Activity deleteModuleFromActivity(Long activityId, Long activityModuleId) {
        var activity = activityRepository.getById(activityId);
        var activityModule = activityModuleRepository.getById(activityModuleId);
        activity.removeActivityModule(activityModule);
        activityRepository.save(activity);
        return activity;
    }

}
