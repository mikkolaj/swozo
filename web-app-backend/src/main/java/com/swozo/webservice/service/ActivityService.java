package com.swozo.webservice.service;

import com.swozo.api.auth.dto.AppRole;
import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.users.Role;
import com.swozo.databasemodel.users.User;
import com.swozo.repository.RoleRepository;
import com.swozo.repository.UserRepository;
import com.swozo.webservice.exceptions.ActivityModuleNotFoundException;
import com.swozo.webservice.exceptions.ActivityNotFoundException;
import com.swozo.webservice.exceptions.CourseNotFoundException;
import com.swozo.webservice.repository.ActivityModuleRepository;
import com.swozo.webservice.repository.ActivityRepository;
import com.swozo.webservice.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@Service
public class ActivityService {
    private final ActivityRepository activityRepository;
    private final ActivityModuleRepository activityModuleRepository;
    private final CourseRepository courseRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityRepository activityRepository,
                           ActivityModuleRepository activityModuleRepository, CourseRepository courseRepository, RoleRepository roleRepository, UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.activityModuleRepository = activityModuleRepository;
        this.courseRepository = courseRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;

//        Arrays.stream(AppRole.values())
//                .map(AppRole::toString)
//                .filter(name -> roleRepository.findByName(name) == null)
//                .forEach(name -> roleRepository.save(new Role(name)));
//
//        Course c1 = new Course("kurs2");
//        c1.setDescription("opis2");
//        c1.setSubject("INFORMATYKA2");
////        System.out.println(11111);
//        Role teacherRole = this.roleRepository.findByName(AppRole.TEACHER.toString());
//        User teacher = new User("e-mail2", "haslo2", List.of(teacherRole));
//        this.userRepository.save(teacher);
////        System.out.println(22222);
//
//        courseRepository.save(c1);
//
////        System.out.println(33333);
//
//        Activity a1 = new Activity();
//        a1.setName("activity1");
//        a1.setDescription("descrpition for activity1");
//        a1.setInstructionsFromTeacher("instruction !!!!");
//        a1.setCourse(c1);
//
//        createActivity(a1);


    }

    public Activity getActivity(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
    }

    public Activity createActivity(Activity newActivity) {
        newActivity.getModules().forEach(activityModule -> activityModule.setActivity(newActivity));
        Long id = newActivity.getCourse().getId();
        System.out.println("kurs id: " + id);
        Course course = courseRepository.findById(newActivity.getCourse().getId())
                .orElseThrow(() -> new CourseNotFoundException(newActivity.getCourse().getId()));
        course.addActivity(newActivity);
        System.out.println("kurs: " + course);
//        courseRepository.save(course);
        activityRepository.save(newActivity);
//        here we will add orchestrator communication....
        return newActivity;
    }

    public void deleteActivity(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
        Course course = courseRepository.findById(activity.getCourse().getId())
                .orElseThrow(() -> new CourseNotFoundException(activity.getCourse().getId()));
        course.deleteActivity(activity);
        courseRepository.save(course);
        activityRepository.deleteById(id);
    }

    public Activity updateActivity(Long id, Activity newActivity) {
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

    public Collection<ActivityModule> activityModulesList(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
        return activity.getModules();
    }

    public Activity addModuleToActivity(Long activityId, Long moduleId) {
        Activity activity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ActivityNotFoundException(activityId));
        ActivityModule activityModule = activityModuleRepository.findById(moduleId)
                .orElseThrow(() -> new ActivityModuleNotFoundException(moduleId));
        activity.addActivityModule(activityModule);
        activityRepository.save(activity);
        return activity;
    }

    public Activity deleteModuleFromActivity(Long activityId, Long moduleId) {
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

    public Collection<String> linksList(Long id) {
        Activity activity = activityRepository.findById(id)
                .orElseThrow(() -> new ActivityNotFoundException(id));
//        TODO cos trzeba zmienić bo linki tryzmamy w activity modules
        return new LinkedList<>();
    }

}
