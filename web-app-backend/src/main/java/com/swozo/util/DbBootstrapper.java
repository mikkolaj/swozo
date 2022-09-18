package com.swozo.util;

import com.swozo.api.web.activity.ActivityRepository;
import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.CourseRepository;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.user.RoleRepository;
import com.swozo.api.web.user.UserRepository;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.persistence.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DbBootstrapper implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(DbBootstrapper.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
    private final ServiceModuleRepository serviceModuleRepository;
    private final ActivityModuleRepository activityModuleRepository;
    private boolean alreadySetup = false;

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
        Arrays.stream(RoleDto.values())
                .map(RoleDto::toString)
                .filter(name -> roleRepository.findByName(name) == null)
                .forEach(name -> roleRepository.save(new Role(name)));
    }

    // TODO assert dev env
    private void setupTestData() {
        var adminRole = roleRepository.findByName(RoleDto.ADMIN.toString());
        userRepository.save(new User("Bolek", "Kowalski", "admin@gmail.com", "admin", List.of(adminRole)));

        var teacherRole = roleRepository.findByName(RoleDto.TEACHER.toString());
        var technicalTeacherRole = roleRepository.findByName(RoleDto.TECHNICAL_TEACHER.toString());
        User teacher = new User("Lolek", "Kowalski", "teacher@gmail.com", "teacher", List.of(teacherRole, technicalTeacherRole));
        userRepository.save(teacher);

        var studentRole = roleRepository.findByName(RoleDto.STUDENT.toString());
        User student1 = new User("Antoni", "Zabrzydowski", "student1@gmail.com", "student1", List.of(studentRole));
        userRepository.save(student1);
        User student2 = new User("Mela", "Zabrzydowska", "student2@gmail.com", "student2", List.of(studentRole));
        userRepository.save(student2);

//        COURSES:
        Course course = new Course();
        course.setName("Programowanie w języku Python");
        course.setSubject("INFORMATYKA");
        course.setDescription("kurs o pythonie");
        course.setTeacher(teacher);
        course.setPassword("haslo");
        course.setStudents(List.of(new UserCourseData(student1, course), new UserCourseData(student2, course)));

        courseRepository.save(course);

//        ServiceModule
        ServiceModule serviceModule = new ServiceModule();
        serviceModule.setName("Klasy w Pythonie");
        serviceModule.setInstructionsFromTechnicalTeacher("instrukcja do klas w pythonie trzeba miec klase");
        serviceModule.setCreatorName("Boleslaw");
        serviceModule.setSubject("INFORMATYKA");
        serviceModule.setScheduleType(ScheduleType.JUPYTER);
        serviceModule.setCreationTime(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule);

        ServiceModule serviceModule2 = new ServiceModule();
        serviceModule2.setName("Funkcje w Pythonie");
        serviceModule2.setInstructionsFromTechnicalTeacher("instrukcja do funkcji w pythonie trzeba funkcjonowac");
        serviceModule2.setCreatorName("Boleslaw");
        serviceModule2.setSubject("INFORMATYKA");
        serviceModule2.setScheduleType(ScheduleType.JUPYTER);
        serviceModule2.setCreationTime(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule2);

//        ACTIVITIES:
        Activity activity = new Activity();
        activity.setName("Pętle, konstrukcje warunkowe");
        activity.setDescription("podstawy");
        activity.setStartTime(LocalDateTime.of(2022,
                Month.JULY, 29, 17, 30, 40));
        activity.setEndTime(LocalDateTime.of(2022,
                Month.JULY, 29, 19, 30, 40));
        activity.setInstructionsFromTeacher(List.of(new ActivityInstruction( "Przed zajęciami należy przeczytać dokumentacje Pythona")));
        activity.setCourse(course);
        activity.addActivityModule(new ActivityModule(
                serviceModule,
                activity,
                "1. Wejdź w link\n2. Wpisz podany wyżej login i hasło w formularzu\n3. Otwórz zakładkę pliki",
                List.of(new ActivityLink("http://34.118.97.16/lab", "Login: student@123.swozo.com\nHasło: 123123")
                )));
        course.addActivity(activity);
        activityRepository.save(activity);

        Activity activity2 = new Activity();
        activity2.setName("Machine Learning");
        activity2.setDescription("podstawy");
        activity2.setStartTime(LocalDateTime.of(2022,
                Month.JULY, 30, 15, 30, 40));
        activity2.setEndTime(LocalDateTime.of(2022,
                Month.JULY, 30, 17, 0, 40));
        activity2.setInstructionsFromTeacher(List.of(new ActivityInstruction("Przed zajęciami należy przeczytać dokumentacje Pythona")));
        activity2.setCourse(course);
        activity2.addActivityModule(new ActivityModule(
                serviceModule,
                activity,
                "1. Wejdź w link\n2. Wpisz podany wyżej login i hasło w formularzu\n3. Otwórz zakładkę pliki",
                List.of(new ActivityLink("http://34.118.97.16/lab", "Login: student@123.swozo.com\nHasło: 123123")
                )));
        course.addActivity(activity2);
        activityRepository.save(activity2);


//        ActivityModule
        ActivityModule activityModule = new ActivityModule(
                serviceModule,
                activity,
                "instrukcja",
                new LinkedList<>()
        );
        activityModuleRepository.save(activityModule);
    }
}
