package com.swozo.util;

import com.swozo.api.common.files.FileRepository;
import com.swozo.api.web.activity.ActivityRepository;
import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.CourseRepository;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.user.RoleRepository;
import com.swozo.api.web.user.UserRepository;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.persistence.Course;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.ServiceModule;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityLink;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
import com.swozo.persistence.user.Role;
import com.swozo.persistence.user.User;
import com.swozo.persistence.user.UserCourseData;
import com.swozo.utils.SupportedLanguage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DbBootstrapper implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(DbBootstrapper.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
    private final AuthService authService;
    private final ServiceModuleRepository serviceModuleRepository;
    private final ActivityModuleRepository activityModuleRepository;
    private final FileRepository fileRepository;
    @Value("${database.enable-bootstrapping}")
    private final boolean enableBootstrapping;
    private boolean alreadySetup = false;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // see https://www.baeldung.com/role-and-privilege-for-spring-security-registration for alreadySetup motivation
        if (this.alreadySetup)
            return;
        logger.info("preparing database...");

        prepareRoles();

        if (enableBootstrapping) {
            setupTestData();
        }

        logger.info("database ready");
        alreadySetup = true;
    }

    private void prepareRoles() {
        Arrays.stream(RoleDto.values())
                .map(RoleDto::toString)
                .filter(name -> roleRepository.findByName(name) == null)
                .forEach(name -> roleRepository.save(new Role(name)));
    }

    private void setupTestData() {
        var adminRole = roleRepository.findByName(RoleDto.ADMIN.toString());
        userRepository.save(new User("Bolek", "Kowalski", "admin@gmail.com", authService.hashPassword("admin"), List.of(adminRole)));

        var teacherRole = roleRepository.findByName(RoleDto.TEACHER.toString());
        var technicalTeacherRole = roleRepository.findByName(RoleDto.TECHNICAL_TEACHER.toString());
        User teacher = new User("Lolek", "Kowalski", "teacher@gmail.com", authService.hashPassword("t"), List.of(teacherRole, technicalTeacherRole));
        User teacher2 = new User("Bolek", "Kowalski", "teacher2@gmail.com", authService.hashPassword("t"), List.of(teacherRole, technicalTeacherRole));
        userRepository.save(teacher);
        userRepository.save(teacher2);

        var studentRole = roleRepository.findByName(RoleDto.STUDENT.toString());
        User student1 = new User("Antoni", "Zabrzydowski", "student1@gmail.com", authService.hashPassword("s"), List.of(studentRole));
        student1.setChangePasswordToken("test");
        userRepository.save(student1);
        User student2 = new User("Mela", "Zabrzydowska", "student2@gmail.com", authService.hashPassword("s"), List.of(studentRole));
        userRepository.save(student2);
        User student3 = new User("Rafał", "Zabrzydowski", "student3@gmail.com", authService.hashPassword("s"), List.of(studentRole));
        userRepository.save(student3);

        //        COURSES:
        Course course = new Course();
        course.setName("Programowanie w języku Python");
        course.setSubject("INFORMATYKA");
        course.setDescription("kurs o pythonie");
        course.setTeacher(teacher);
        course.setPassword("haslo");
        course.setJoinUUID(UUID.randomUUID().toString());
        course.setStudents(List.of(new UserCourseData(student1, course), new UserCourseData(student2, course)));
        course.setSandboxMode(false);
        course.setPublic(false);
        courseRepository.save(course);

        //        FILES
        var mockFile = new RemoteFile();
        mockFile.setId(0L);
        mockFile.setPath("lab_file.ipynb");
        mockFile.setSizeBytes(2000L);

        fileRepository.save(mockFile);

        //        ServiceModule
        ServiceModule serviceModule = new ServiceModule();
        serviceModule.setName("Klasy w Pythonie");
        serviceModule.setTeacherInstructionHtml("teach");
        serviceModule.setStudentInstructionHtml("stud");
        serviceModule.setCreator(teacher);
        serviceModule.setDescription("opis1");
        serviceModule.setSubject("INFORMATYKA");
        serviceModule.setScheduleTypeName(ScheduleType.JUPYTER.toString());
        serviceModule.setDynamicProperties(Map.of("notebookLocation", mockFile.getId().toString()));
        serviceModule.setPublic(true);
        serviceModule.setReady(true);
        serviceModule.setCreatedAt(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule);

        ServiceModule serviceModule2 = new ServiceModule();
        serviceModule2.setName("Funkcje w Pythonie");
        serviceModule2.setTeacherInstructionHtml("teach");
        serviceModule2.setStudentInstructionHtml("stud");
        serviceModule2.setCreator(teacher);
        serviceModule2.setDescription("opis2");
        serviceModule2.setSubject("INFORMATYKA");
        serviceModule2.setScheduleTypeName(ScheduleType.JUPYTER.toString());
        serviceModule2.setDynamicProperties(Map.of("notebookLocation", mockFile.getId().toString()));
        serviceModule2.setPublic(true);
        serviceModule2.setReady(true);
        serviceModule2.setCreatedAt(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule2);

        var activityLink1 = new ActivityLink();
        activityLink1.setUrl("http://34.118.97.16/lab");
        activityLink1.setTranslation(new TranslatableActivityLink(SupportedLanguage.PL, "Login: student@123.swozo.com\nHasło: 123123"));
        activityLink1.setTranslation(new TranslatableActivityLink(SupportedLanguage.EN, "en test"));

        //        ACTIVITIES:
        Activity activity = new Activity();
        activity.setName("Pętle, konstrukcje warunkowe");
        activity.setDescription("podstawy");
        activity.setStartTime(LocalDateTime.of(2022,
                Month.JULY, 29, 17, 30, 40));
        activity.setEndTime(LocalDateTime.of(2022,
                Month.JULY, 29, 19, 30, 40));
        activity.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity.setCourse(course);
        activity.addActivityModule(new ActivityModule(
                serviceModule,
                activity,
                9999999L,
                List.of(activityLink1)
        ));
        course.addActivity(activity);
        activityRepository.save(activity);

        Activity activity2 = new Activity();
        activity2.setName("Machine Learning");
        activity2.setDescription("podstawy");
        activity2.setStartTime(LocalDateTime.of(2022,
                Month.JULY, 30, 15, 30, 40));
        activity2.setEndTime(LocalDateTime.of(2022,
                Month.JULY, 30, 17, 0, 40));
        activity2.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity2.setCourse(course);
        activity2.addActivityModule(new ActivityModule(
                serviceModule,
                activity2,
                99999L,
                List.of(activityLink1)
        ));
        course.addActivity(activity2);
        activityRepository.save(activity2);
    }
}
