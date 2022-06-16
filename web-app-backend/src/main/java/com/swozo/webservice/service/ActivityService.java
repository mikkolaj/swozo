package com.swozo.webservice.service;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.databasemodel.Course;
import com.swozo.webservice.exceptions.ActivityModuleNotFoundException;
import com.swozo.webservice.exceptions.ActivityNotFoundException;
import com.swozo.webservice.repository.ActivityModuleRepository;
import com.swozo.webservice.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
public class ActivityService {
    private final CourseService courseService;
    private final ActivityRepository activityRepository;
    private final ActivityModuleRepository activityModuleRepository;

    public ActivityService(CourseService courseService,
                           ActivityRepository activityRepository,
                           ActivityModuleRepository activityModuleRepository) {
        this.courseService = courseService;
        this.activityRepository = activityRepository;
        this.activityModuleRepository = activityModuleRepository;
    }

    public Activity getActivity(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
    }

    public Activity createActivity(Activity newActivity) {
        newActivity.getModules().forEach(activityModule -> activityModule.setActivity(newActivity));
        Long courseID = newActivity.getCourse().getId();
        Course course = courseService.getCourse(courseID);
        course.addActivity(newActivity);
        newActivity.setCourse(course);
        activityRepository.save(newActivity);
//        here we will add orchestrator communication....
        return newActivity;
    }

    public void deleteActivity(Long id) {
        Activity activity = getActivity(id);
        Course course = courseService.getCourse(activity.getCourse().getId());
        course.deleteActivity(activity);
        activityRepository.deleteById(id);
    }

    public Activity updateActivity(Long id, Activity newActivity) {
        Activity activity = getActivity(id);
        activity.setName(newActivity.getName());
        activity.setDescription(newActivity.getDescription());
        activity.setDateTime(newActivity.getDateTime());
        activity.setInstructionsFromTeacher(newActivity.getInstructionsFromTeacher());
        activityRepository.save(activity);
        return activity;
    }

    public Collection<ActivityModule> activityModulesList(Long id) {
        Activity activity = getActivity(id);
        return activity.getModules();
    }

    public Activity addModuleToActivity(Long activityId, Long activityModuleId) {
        Activity activity = getActivity(activityId);

        //TODO replace with activityModuleService.get(id) when is ready
        ActivityModule activityModule = activityModuleRepository.findById(activityModuleId)
                .orElseThrow(() -> new ActivityModuleNotFoundException(activityModuleId));

        activity.addActivityModule(activityModule);
        activityRepository.save(activity);
        return activity;
    }

    public Activity deleteModuleFromActivity(Long activityId, Long activityModuleId) {
        Activity activity = getActivity(activityId);
        ActivityModule activityModule = activityModuleRepository.findById(activityModuleId)
                .orElseThrow(() -> new ActivityModuleNotFoundException(activityModuleId));
        activity.removeActivityModule(activityModule);
        activityRepository.save(activity);
        return activity;
    }

}
