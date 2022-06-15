package com.swozo.webservice.service;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.webservice.exceptions.ActivityModuleNotFoundException;
import com.swozo.webservice.exceptions.ActivityNotFoundException;
import com.swozo.webservice.repository.ActivityModuleRepository;
import com.swozo.webservice.repository.ActivityRepository;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityModuleRepository activityModuleRepository;

    public ActivityService(ActivityRepository activityRepository,
                           ActivityModuleRepository activityModuleRepository) {
        this.activityRepository = activityRepository;
        this.activityModuleRepository = activityModuleRepository;
    }

    public Activity getActivity(long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
    }

    public Activity createActivity(Activity newActivity) {
        newActivity.getModules().forEach(activityModule -> activityModule.setActivity(newActivity));
        activityRepository.save(newActivity);
        return newActivity;
    }

    public void deleteActivity(long id) {
        activityRepository.deleteById(id);
    }

    public Activity updateActivity(long id, Activity newActivity) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
        activity.setName(newActivity.getName());
        activity.setDescription(newActivity.getDescription());
        activity.setDateTime(newActivity.getDateTime());
        activity.setInstructionsFromTeacher(activity.getInstructionsFromTeacher());
        Collection<ActivityModule> activityModules = newActivity.getModules();
        activityModules.forEach(activityModule -> activityModule.setActivity(newActivity));
        activity.setModules(activityModules);
        activity.setCourse(newActivity.getCourse());
        activityRepository.save(activity);
        return activity;
    }

    public Collection<ActivityModule> activityModulesList(long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
        return activity.getModules();
    }

    public Activity addModuleToActivity(long activityId, long moduleId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        ActivityModule activityModule = activityModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ActivityModuleNotFoundException(moduleId));
        activity.addActivityModule(activityModule);
        activityRepository.save(activity);
        return activity;
    }

    public Activity deleteModuleFromActivity(long activityId, long moduleId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        ActivityModule activityModule = activityModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ActivityModuleNotFoundException(moduleId));
        Collection<ActivityModule> activityModules = activity.getModules();
        activityModules.remove(activityModule);
        activity.setModules(activityModules);
        activityRepository.save(activity);
        return activity;
    }

    public Collection<String> linksList(long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
//        TODO cos trzeba zmienić bo linki tryzmamy w activity modules
        return new LinkedList<>();
    }

}
