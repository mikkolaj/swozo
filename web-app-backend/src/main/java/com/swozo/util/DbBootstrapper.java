package com.swozo.util;

import com.swozo.api.common.files.FileRepository;
import com.swozo.api.web.activity.ActivityRepository;
import com.swozo.api.web.activitymodule.ActivityModuleService;
import com.swozo.api.web.auth.AuthService;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.CourseRepository;
import com.swozo.api.web.mda.policy.PolicyRepository;
import com.swozo.api.web.mda.policy.PolicyService;
import com.swozo.api.web.mda.vm.VmRepository;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.user.RoleRepository;
import com.swozo.api.web.user.UserRepository;
import com.swozo.model.scheduling.properties.ServiceType;
import com.swozo.persistence.Course;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.activity.ActivityModuleScheduleInfo;
import com.swozo.persistence.activity.UserActivityModuleInfo;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
import com.swozo.persistence.mda.VirtualMachine;
import com.swozo.persistence.mda.policies.Policy;
import com.swozo.persistence.mda.policies.PolicyType;
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
    private final CourseRepository courseRepository;
    private final ActivityRepository activityRepository;
    private final AuthService authService;
    private final ServiceModuleRepository serviceModuleRepository;
    private final FileRepository fileRepository;
    private final PolicyRepository policyRepository;
    private final VmRepository vmRepository;
    private final PolicyService policyService;
    private final ActivityModuleService activityModuleService;
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
        course.setExpectedStudentCount(2);
        course.setJoinUUID(UUID.randomUUID().toString());
        course.setStudents(List.of(new UserCourseData(student1, course), new UserCourseData(student2, course)));
        course.setSandboxMode(false);
        course.setPublic(false);
        courseRepository.save(course);

        //        FILES
        var jupyterFile = new RemoteFile();
        jupyterFile.setPath("lab_file.ipynb");
        jupyterFile.setSizeBytes(2000L);
        jupyterFile.setOwner(teacher);

        jupyterFile = fileRepository.save(jupyterFile);

        var quizFile = new RemoteFile();
        quizFile.setPath("questions.yaml");
        quizFile.setSizeBytes(463L);
        quizFile.setOwner(teacher);

        quizFile = fileRepository.save(quizFile);

        //        ServiceModule
        ServiceModule serviceModule = new IsolatedServiceModule();
        serviceModule.setBaseBandwidthMbps(1);
        serviceModule.setBaseRamGB(1);
        serviceModule.setBaseVcpu(1);
        serviceModule.setBaseDiskGB(1);
        serviceModule.setName("Klasy w Pythonie (JUPYTER TEST)");
        serviceModule.setTeacherInstructionHtml("teach");
        serviceModule.setStudentInstructionHtml("stud");
        serviceModule.setCreator(teacher);
        serviceModule.setDescription("opis1");
        serviceModule.setSubject("INFORMATYKA");
        serviceModule.setServiceName(ServiceType.JUPYTER.toString());
        serviceModule.setDynamicProperties(Map.of("notebookLocation", jupyterFile.getId().toString()));
        serviceModule.setPublic(true);
        serviceModule.setReady(true);
        serviceModule.setCreatedAt(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule);

        SharedServiceModule serviceModule2 = new SharedServiceModule();
        serviceModule2.setBaseBandwidthMbps(1);
        serviceModule2.setBaseRamGB(1);
        serviceModule2.setBaseVcpu(1);
        serviceModule2.setBaseDiskGB(1);
        serviceModule2.setUsersPerAdditionalDiskGb(10);
        serviceModule2.setUsersPerAdditionalCore(10);
        serviceModule2.setUsersPerAdditionalRamGb(10);
        serviceModule2.setUsersPerAdditionalBandwidthGbps(10);
        serviceModule2.setName("super quiz (QuizApp)");
        serviceModule2.setTeacherInstructionHtml("teach");
        serviceModule2.setStudentInstructionHtml("stud");
        serviceModule2.setCreator(teacher);
        serviceModule2.setDescription("opis2");
        serviceModule2.setSubject("Inne");
        serviceModule2.setServiceName(ServiceType.QUIZAPP.toString());
        serviceModule2.setDynamicProperties(Map.of(
                "questionsLocation", quizFile.getId().toString(),
                "quizDurationSeconds", "120")
        );
        serviceModule2.setPublic(true);
        serviceModule2.setReady(true);
        serviceModule2.setCreatedAt(LocalDateTime.of(2022,
                Month.MAY, 29, 21, 30, 40));
        serviceModuleRepository.save(serviceModule2);

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

        Activity activity = new Activity();
        activity.setName("Pętle, konstrukcje warunkowe");
        activity.setDescription("podstawy");
        activity.setStartTime(LocalDateTime.of(2022,
                Month.JULY, 29, 17, 30, 40));
        activity.setEndTime(LocalDateTime.of(2022,
                Month.JULY, 29, 19, 30, 40));
        activity.setInstructionFromTeacherHtml("Przed zajęciami należy przeczytać dokumentacje Pythona");
        activity.addActivityModule(activityModule1);
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
        activity2.addActivityModule(activityModule2);
        course.addActivity(activity2);
        activityRepository.save(activity2);

//        POLICIES:
        var teacher1 = userRepository.getByEmail("teacher@gmail.com");
        Policy policy = new Policy();
        policy.setPolicyType(PolicyType.MAX_RAM_GB);
        policy.setTeacher(teacher1);
        policy.setValue(20);

        policyRepository.save(policy);

        VirtualMachine vm1 = new VirtualMachine("e2-medium", 2, 4, 2048, 10, "");
        vmRepository.save(vm1);
        VirtualMachine vm2 = new VirtualMachine("e2-standard-4", 4, 16, 8192, 10, "");
        vmRepository.save(vm2);
        VirtualMachine vm3 = new VirtualMachine("e2-standard-8", 8, 32, 16384, 10, "");
        vmRepository.save(vm3);

        policyRepository.saveAll(policyService.createDefaultTeacherPolicies(teacher1));
        policyRepository.saveAll(policyService.createDefaultTeacherPolicies(teacher2));
    }
}
