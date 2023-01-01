package com.swozo.util;

import com.swozo.api.common.files.FileRepository;
import com.swozo.api.web.activity.ActivityRepository;
import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.CourseRepository;
import com.swozo.api.web.mda.policy.PolicyRepository;
import com.swozo.api.web.mda.policy.PolicyService;
import com.swozo.api.web.mda.vm.VmRepository;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.user.RoleRepository;
import com.swozo.api.web.user.UserAdminService;
import com.swozo.api.web.user.UserRepository;
import com.swozo.api.web.user.request.CreateUserRequest;
import com.swozo.config.properties.ApplicationProperties;
import com.swozo.model.scheduling.properties.ServiceType;
import com.swozo.persistence.Course;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.activity.ActivityModuleScheduleInfo;
import com.swozo.persistence.activity.UserActivityModuleInfo;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
import com.swozo.persistence.mda.VirtualMachine;
import com.swozo.persistence.servicemodule.IsolatedServiceModule;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.servicemodule.SharedServiceModule;
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
    private final UserAdminService userAdminService;
    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
    private final AuthService authService;
    private final ServiceModuleRepository serviceModuleRepository;
    private final FileRepository fileRepository;
    private final PolicyRepository policyRepository;
    private final VmRepository vmRepository;
    private final PolicyService policyService;
    @Value("${database.enable-bootstrapping}")
    private final boolean enableBootstrapping;
    private final ApplicationProperties applicationProperties;
    private boolean alreadySetup = false;

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // see https://www.baeldung.com/role-and-privilege-for-spring-security-registration for alreadySetup motivation
        if (this.alreadySetup)
            return;
        logger.info("preparing database...");

        prepareRoles();
        prepareAdminAccount();

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

    private void prepareAdminAccount() {
        if (userRepository.findAll().isEmpty()) {
            var admin = applicationProperties.initialAdmin();
            logger.info("Creating admin account for {}", admin);
            userAdminService.createUser(new CreateUserRequest(
                    admin.name(),
                    admin.surname(),
                    admin.email(),
                    List.of(RoleDto.ADMIN)
            ));
        }
    }

    private void setupTestData() {
        var adminRole = roleRepository.findByName(RoleDto.ADMIN.toString());
        userRepository.save(new User("Włodzimierz", "Biały", "wbialy@gmail.com", authService.hashPassword("admin"), List.of(adminRole)));

        var teacherRole = roleRepository.findByName(RoleDto.TEACHER.toString());
        var technicalTeacherRole = roleRepository.findByName(RoleDto.TECHNICAL_TEACHER.toString());
        User teacher = new User("Lolek", "Kowalski", "lkowalski@gmail.com", authService.hashPassword("teacher"), List.of(teacherRole, technicalTeacherRole));
        User teacher2 = new User("Bolek", "Zagórski", "bzagorski@gmail.com", authService.hashPassword("teacher"), List.of(teacherRole, technicalTeacherRole));
        userRepository.save(teacher);
        userRepository.save(teacher2);

        var studentRole = roleRepository.findByName(RoleDto.STUDENT.toString());
        User student1 = new User("Antoni", "Zabrzydowski", "azabrzydowski@gmail.com", authService.hashPassword("student"), List.of(studentRole));
        student1.setChangePasswordToken("test");
        userRepository.save(student1);
        User student2 = new User("Mela", "Nowak", "mnowak@gmail.com", authService.hashPassword("student"), List.of(studentRole));
        userRepository.save(student2);
        User student3 = new User("Rafał", "Przepióra", "rprzepiora@gmail.com", authService.hashPassword("student"), List.of(studentRole));
        userRepository.save(student3);

        //        COURSES:
        Course course = new Course();
        course.setName("Programowanie w języku Python");
        course.setSubject("Informatyka");
        course.setDescription("Kurs uczy od podstaw programowania w jęzuku Python.");
        course.setTeacher(teacher);
        course.setPassword("haslo");
        course.setExpectedStudentCount(2);
        course.setJoinUUID(UUID.randomUUID().toString());
        course.setStudents(List.of(new UserCourseData(student1, course), new UserCourseData(student2, course)));
        course.setSandboxMode(false);
        course.setPublic(false);
        courseRepository.save(course);

        Course course2 = new Course();
        course2.setName("Systemy rekomendacyjne");
        course2.setSubject("Informatyka");
        course2.setDescription("Kurs omawia zagadnienia związane z budową systemów rekomendacyjnych");
        course2.setTeacher(teacher);
        course2.setPassword("haslo");
        course2.setExpectedStudentCount(2);
        course2.setJoinUUID(UUID.randomUUID().toString());
        course2.setStudents(List.of(new UserCourseData(student1, course2), new UserCourseData(student2, course2)));
        course2.setSandboxMode(false);
        course2.setPublic(false);
        courseRepository.save(course2);


        //        FILES
        var jupyterFile = new RemoteFile();
        jupyterFile.setPath("lab_file.ipynb");
        jupyterFile.setSizeBytes(2000L);
        jupyterFile.setOwner(teacher);

        jupyterFile = fileRepository.save(jupyterFile);

        var quizFile = new RemoteFile();
        quizFile.setPath("questions.yaml");
        quizFile.setSizeBytes(673L);
        quizFile.setOwner(teacher);

        quizFile = fileRepository.save(quizFile);

        //        ServiceModule
        ServiceModule serviceModule = new IsolatedServiceModule();
        serviceModule.setBaseBandwidthMbps(512);
        serviceModule.setBaseRamGB(2);
        serviceModule.setBaseVcpu(1);
        serviceModule.setBaseDiskGB(8);
        serviceModule.setName("Klasy w Pythonie");
        serviceModule.setTeacherInstructionHtml("<h3>Upewnij się, że przed tym modułem uczniowie znają pojęcia takie jak:</h3><ul><li>zmienna</li><li>pętle</li><li>konstrukcje warunkowe</li></ul><p><br></p><p>i potrafią się&nbsp;nimi biegle posługiwać w języku Python.</p>");
        serviceModule.setStudentInstructionHtml("<p>Przypomnij sobie wiedzę o zmiennych, pętlach i konstrukcjach warunkowy. Zapoznaj się z wykładem omawiającym najważniejsze pojęcia związane z programowaniem obiektowym.</p>");
        serviceModule.setCreator(teacher);
        serviceModule.setDescription("Moduł uczy podstaw programowania obiektowego w języku Python.");
        serviceModule.setSubject("Informatyka");
        serviceModule.setServiceName(ServiceType.JUPYTER.toString());
        serviceModule.setDynamicProperties(Map.of("notebookLocation", jupyterFile.getId().toString()));
        serviceModule.setServiceDisplayName("Jupyter Notebook");
        serviceModule.setPublic(true);
        serviceModule.setReady(true);
        serviceModule.setCreatedAt(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule);

        SharedServiceModule serviceModule3 = new SharedServiceModule();
        serviceModule3.setBaseBandwidthMbps(128);
        serviceModule3.setBaseRamGB(1);
        serviceModule3.setBaseVcpu(1);
        serviceModule3.setBaseDiskGB(4);
        serviceModule3.setUsersPerAdditionalBandwidthGbps(50);
        serviceModule3.setUsersPerAdditionalRamGb(50);
        serviceModule3.setUsersPerAdditionalCore(50);
        serviceModule3.setUsersPerAdditionalDiskGb(200);
        serviceModule3.setName("Quiz z podstaw kryptografii");
        serviceModule3.setTeacherInstructionHtml("<p>Uczniowie powinni znać szyfr <strong>cezara</strong> i mieć ogólną wiedzę dotyczącą <strong>szyfrów monoalfabetycznych</strong>.</p><p>Konieczna jest ponadto znajomość pojęć takich jak:</p><ul><li>szyfrogram</li><li>tekst jawny</li></ul>");
        serviceModule3.setStudentInstructionHtml("<p>Quiz składa się&nbsp;z 3 pytań jednokrotnego wyboru, na odpowiedź będziesz miał<strong> 90 sekund</strong>. Warto wcześniej zapoznać się&nbsp;z wykładem.</p>");
        serviceModule3.setCreator(teacher2);
        serviceModule3.setDescription("Moduł ma na celu sprawdzenie wiedzy z podstawowych metod szyfrowania.");
        serviceModule3.setSubject("Informatyka");
        serviceModule3.setServiceName(ServiceType.QUIZAPP.toString());
        serviceModule3.setServiceDisplayName("QuizApp");
        serviceModule3.setDynamicProperties(Map.of(
                "questionsLocation", quizFile.getId().toString(),
                "quizDurationSeconds", "90")
        );
        serviceModule3.setPublic(true);
        serviceModule3.setReady(true);
        serviceModule3.setCreatedAt(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule3);

        SharedServiceModule serviceModule4 = new SharedServiceModule();
        serviceModule4.setBaseBandwidthMbps(1024);
        serviceModule4.setBaseRamGB(1);
        serviceModule4.setBaseVcpu(1);
        serviceModule4.setBaseDiskGB(1);
        serviceModule4.setUsersPerAdditionalBandwidthGbps(20);
        serviceModule4.setUsersPerAdditionalRamGb(50);
        serviceModule4.setUsersPerAdditionalCore(20);
        serviceModule4.setUsersPerAdditionalDiskGb(100);
        serviceModule4.setName("Wideokonferencja");
        serviceModule4.setTeacherInstructionHtml("<p>Widekonferencja ogólnego przeznaczenia, należy mieć mikrofon i opcjonalnie kamerke. Jako nauczyciel masz dodatkowo możliwość wyciszania innych uczestników.</p>");
        serviceModule4.setStudentInstructionHtml("<p>Widekonferencja ogólnego przeznaczenia, należy mieć mikrofon i opcjonalnie kamerke.</p>");
        serviceModule4.setCreator(teacher);
        serviceModule4.setDescription("Wideokonferencja ogólnego przeznaczenia.");
        serviceModule4.setSubject("Dowolny");
        serviceModule4.setServiceName(ServiceType.SOZISEL.toString());
        serviceModule4.setServiceDisplayName("Jitsi Meet");
        serviceModule4.setPublic(true);
        serviceModule4.setReady(true);
        serviceModuleRepository.save(serviceModule4);

        IsolatedServiceModule serviceModule5 = new IsolatedServiceModule();
        serviceModule5.setBaseBandwidthMbps(128);
        serviceModule5.setBaseRamGB(1);
        serviceModule5.setBaseVcpu(1);
        serviceModule5.setBaseDiskGB(1);
        serviceModule5.setName("Podstawy bezpieczeństwa aplikacji webowych");
        serviceModule5.setTeacherInstructionHtml("<p>Moduł z bezpieczeństwa aplikacji webowych. Uczniowie powinni mieć dobre podstawy z programowania w języku <strong>JavaScript</strong> oraz znać podstawy języka <strong>PHP</strong>. Należy również biegle operować językiem <strong>SQL</strong>.</p><p><br></p><p>Warto również wcześniej omówić pojęcia takie jak:</p><ul><li>SQL injection</li><li>CSRF</li><li>XSS</li></ul>");
        serviceModule5.setStudentInstructionHtml("<p>Dostaniesz link do strony&nbsp;webową z celowo zaprojektowanymi błędami. Twoim zadaniem będzie znalezienie jak najwięcej podatności i wykorzystanie ich na szkodę strony. Będzie ci potrzebna znajomość języka JavaScript, SQL oraz podstaw PHP. Poczytaj wcześniej o podstawowych podatnościach stron typu SQL injection, CSRF oraz XSS.</p>");
        serviceModule5.setCreator(teacher);
        serviceModule5.setDescription("Moduł praktycznie ilustruje najpopularniejsze zagrożenia związane z działaniem aplikacji webowych.");
        serviceModule5.setSubject("Bezpieczeństwo");
        serviceModule5.setServiceName(ServiceType.DOCKER.toString());
        serviceModule5.setServiceDisplayName("Docker");
        serviceModule5.setPublic(true);
        serviceModule5.setReady(true);
        serviceModule5.setDynamicProperties(Map.of(
                "publicImageName", "bkimminich/juice-shop",
                "expectedServiceStartupSeconds", "30",
                "imageSizeMb", "180",
                "portToExpose", "3000"
        ));
        serviceModuleRepository.save(serviceModule5);

        //        ACTIVITIES:

        var activityLink1 = new UserActivityModuleInfo();
        // this hasn't received links yet
        activityLink1.setUser(student1);

        var activityLink2 = new UserActivityModuleInfo();
        activityLink2.setUrl("http://34.118.97.16/lab");
        activityLink2.setTranslation(new TranslatableActivityLink(SupportedLanguage.PL, "Login: student@123.swozo.com\nHasło: 123123"));
        activityLink2.setTranslation(new TranslatableActivityLink(SupportedLanguage.EN, "en test"));
        activityLink2.setUser(teacher);

        var activityModuleScheduleInfo1 = new ActivityModuleScheduleInfo();
        activityModuleScheduleInfo1.setScheduleRequestId(99999L);
        activityModuleScheduleInfo1.addUserActivityLink(activityLink1);
        activityModuleScheduleInfo1.addUserActivityLink(activityLink2);

        var activityLink3 = new UserActivityModuleInfo();
        activityLink3.setUrl("http://34.118.97.16/lab");
        activityLink3.setTranslation(new TranslatableActivityLink(SupportedLanguage.PL, "Login: student@123.swozo.com\nHasło: 123123"));
        activityLink3.setTranslation(new TranslatableActivityLink(SupportedLanguage.EN, "en test"));
        activityLink3.setUser(teacher);

        var activityModuleScheduleInfo2 = new ActivityModuleScheduleInfo();
        activityModuleScheduleInfo2.setScheduleRequestId(99998L);
        activityModuleScheduleInfo2.addUserActivityLink(activityLink3);

        var activityModule1 = new ActivityModule(serviceModule, false);
        activityModule1.addScheduleInfo(activityModuleScheduleInfo1);

        var activityModule2 = new ActivityModule(serviceModule, true);
        activityModule2.addScheduleInfo(activityModuleScheduleInfo2);

        var activityModule3 = new ActivityModule(serviceModule3, true);
        activityModule3.addScheduleInfo(activityModuleScheduleInfo2);

        Activity activity = new Activity();
        activity.setName("Podstawy języka Python. Typy modyfikowalne i niemodyfikowalne.");
        activity.setDescription("podstawy");
        activity.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 1, 17, 30, 40));
        activity.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 1, 19, 30, 40));
        activity.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity.addActivityModule(activityModule1);
        course.addActivity(activity);
        activityRepository.save(activity);

        Activity activity2 = new Activity();
        activity2.setName("Łańcuchy, listy, krotki, zbiory i słowniki. Funkcje i lambdy.");
        activity2.setDescription("podstawy");
        activity2.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 5, 17, 30, 40));
        activity2.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 5, 19, 30, 40));
        activity2.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity2.addActivityModule(activityModule2);
        course.addActivity(activity2);
        activityRepository.save(activity2);

        Activity activity13 = new Activity();
        activity13.setName("Obiektowość w Pythonie. Dziedziczenie.");
        activity13.setDescription("podstawy");
        activity13.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 8, 17, 30, 40));
        activity13.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 8, 19, 30, 40));
        activity13.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity13.addActivityModule(activityModule2);
        course.addActivity(activity13);
        activityRepository.save(activity13);

        Activity activity14 = new Activity();
        activity14.setName("Wyjątki. Generatory. Context manager.");
        activity14.setDescription("podstawy");
        activity14.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 12, 17, 30, 40));
        activity14.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 12, 19, 30, 40));
        activity14.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity14.addActivityModule(activityModule2);
        course.addActivity(activity14);
        activityRepository.save(activity14);

        Activity activity15 = new Activity();
        activity15.setName("Obsługa plików. Dekoratory. Programowanie funkcyjne.");
        activity15.setDescription("podstawy");
        activity15.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 15, 17, 30, 40));
        activity15.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 15, 19, 30, 40));
        activity15.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity15.addActivityModule(activityModule2);
        course.addActivity(activity15);
        activityRepository.save(activity15);

        Activity activity16 = new Activity();
        activity16.setName("Algorytmy oparte na treści - wykład");
        activity16.setDescription("podstawy");
        activity16.setStartTime(LocalDateTime.of(2022,
                Month.NOVEMBER, 28, 9, 40, 40));
        activity16.setEndTime(LocalDateTime.of(2022,
                Month.NOVEMBER, 28, 11, 10, 40));
        activity16.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity16.addActivityModule(activityModule2);
        course2.addActivity(activity16);
        activityRepository.save(activity16);

        Activity activity3 = new Activity();
        activity3.setName("Algorytmy oparte na treści - ćwiczenia");
        activity3.setDescription("podstawy");
        activity3.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 2, 14, 40, 40));
        activity3.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 2, 16, 10, 40));
        activity3.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity3.addActivityModule(activityModule1);
        course2.addActivity(activity3);
        activityRepository.save(activity3);

        Activity activity4 = new Activity();
        activity4.setName("Algorytmy wielorękich bandytów - wykład");
        activity4.setDescription("podstawy");
        activity4.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 5, 9, 40, 40));
        activity4.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 5, 11, 10, 40));
        activity4.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity4.addActivityModule(activityModule2);
        course2.addActivity(activity4);
        activityRepository.save(activity4);

        Activity activity5 = new Activity();
        activity5.setName("Algorytmy wielorękich bandytów - ćwiczenia");
        activity5.setDescription("podstawy");
        activity5.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 9, 14, 40, 40));
        activity5.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 9, 16, 10, 40));
        activity5.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity5.addActivityModule(activityModule2);
        course2.addActivity(activity5);
        activityRepository.save(activity5);

        Activity activity6 = new Activity();
        activity6.setName("Podział użytkowników na grupy według zainteresowań - wykład");
        activity6.setDescription("podstawy");
        activity6.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 12, 9, 40, 40));
        activity6.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 12, 11, 10, 40));
        activity6.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity6.addActivityModule(activityModule2);
        course2.addActivity(activity6);
        activityRepository.save(activity6);

        Activity activity7 = new Activity();
        activity7.setName("Podział użytkowników na grupy według zainteresowań - ćwiczenia");
        activity7.setDescription("podstawy");
        activity7.setStartTime(LocalDateTime.of(2022,
                Month.DECEMBER, 16, 14, 40, 40));
        activity7.setEndTime(LocalDateTime.of(2022,
                Month.DECEMBER, 16, 16, 10, 40));
        activity7.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity7.addActivityModule(activityModule2);
        course2.addActivity(activity7);
        activityRepository.save(activity7);


//        POLICIES:

        VirtualMachine vm1 = new VirtualMachine("e2-medium", 2, 4, 2048, 10, "");
        vmRepository.save(vm1);
        VirtualMachine vm2 = new VirtualMachine("e2-standard-4", 4, 16, 8192, 10, "");
        vmRepository.save(vm2);
        VirtualMachine vm3 = new VirtualMachine("e2-standard-8", 8, 32, 16384, 10, "");
        vmRepository.save(vm3);

        policyRepository.saveAll(policyService.createDefaultTeacherPolicies(teacher));
        policyRepository.saveAll(policyService.createDefaultTeacherPolicies(teacher2));
    }
}
