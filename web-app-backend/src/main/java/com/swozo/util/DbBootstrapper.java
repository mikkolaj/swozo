package com.swozo.util;

import com.swozo.api.auth.dto.AppRole;
import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.Course;
import com.swozo.databasemodel.ServiceModule;
import com.swozo.databasemodel.users.Role;
import com.swozo.databasemodel.users.User;
import com.swozo.repository.RoleRepository;
import com.swozo.repository.UserRepository;
import com.swozo.webservice.repository.ActivityRepository;
import com.swozo.webservice.repository.CourseRepository;
import com.swozo.webservice.repository.ServiceModuleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

@Component
public class DbBootstrapper implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(DbBootstrapper.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
    private final ServiceModuleRepository serviceModuleRepository;
    private boolean alreadySetup;

    @Autowired
    public DbBootstrapper(RoleRepository roleRepository, UserRepository userRepository, CourseRepository courseRepository, ActivityRepository activityRepository, ServiceModuleRepository serviceModuleRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.activityRepository = activityRepository;
        this.serviceModuleRepository = serviceModuleRepository;
        this.alreadySetup = false;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // see https://www.baeldung.com/role-and-privilege-for-spring-security-registration for alreadySetup motivation
        if (this.alreadySetup)
            return;
        logger.info("preparing database...");

        prepareRoles();
        setupTestData();

        logger.info("database ready");
        alreadySetup = true;
    }

    private void prepareRoles() {
        Arrays.stream(AppRole.values())
                .map(AppRole::toString)
                .filter(name -> roleRepository.findByName(name) == null)
                .forEach(name -> roleRepository.save(new Role(name)));
    }

    // TODO assert dev env
    private void setupTestData() {
        var adminRole = roleRepository.findByName(AppRole.ADMIN.toString());
        userRepository.save(new User("Bolek", "Kowalski", "admin", "admin", List.of(adminRole)));

        var teacherRole = roleRepository.findByName(AppRole.TEACHER.toString());
        var technicalTeacherRole = roleRepository.findByName(AppRole.TECHNICAL_TEACHER.toString());
        User teacher = new User("Lolek", "Kowalski", "teacher", "teacher", List.of(teacherRole, technicalTeacherRole));
        userRepository.save(teacher);

        var studentRole = roleRepository.findByName(AppRole.STUDENT.toString());
        User student1 = new User("Antoni", "Zabrzydowski", "student1", "student1", List.of(studentRole));
        userRepository.save(student1);
        User student2 = new User("Mela", "Zabrzydowska", "student2", "student2", List.of(studentRole));
        userRepository.save(student2);

//        COURSES:
        Course course = new Course();
        course.setName("kurs1");
        course.setSubject("INFORMATYKA");
        course.setDescription("opis");
        course.setTeacher(teacher);
        course.addStudent(student1);
        course.addStudent(student2);
        courseRepository.save(course);

//        ACTIVITIES:
        Activity activity = new Activity();
        activity.setName("aktywnosc1");
        activity.setDescription("opis1");
        activity.setStartTime(LocalDateTime.of(2022,
                Month.JULY, 29, 19, 30, 40));
        activity.setEndTime(LocalDateTime.of(2022,
                Month.JULY, 29, 21, 30, 40));
        activity.setInstructionsFromTeacher("instrukcjaaaa");
        activity.setCourse(course);
        course.addActivity(activity);
        activityRepository.save(activity);

//        ServiceModule
        ServiceModule serviceModule = new ServiceModule();
        serviceModule.setName("Jupiter");
        serviceModule.setInstructionsFromTechnicalTeacher("instrukcja");
        serviceModule.setCreatorName("Boleslaw");
        serviceModule.setSubject("INFORMATYKA");
        serviceModule.setCreationTime(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule);



    }
}
