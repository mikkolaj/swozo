package com.swozo.api.web.sandbox;

import com.swozo.api.orchestrator.ScheduleService;
import com.swozo.api.web.activity.dto.SelectedServiceModuleDto;
import com.swozo.api.web.activity.request.CreateActivityRequest;
import com.swozo.api.web.auth.dto.RoleDto;
import com.swozo.api.web.course.CourseRepository;
import com.swozo.api.web.course.CourseService;
import com.swozo.api.web.course.dto.CourseDetailsDto;
import com.swozo.api.web.course.request.CreateCourseRequest;
import com.swozo.api.web.sandbox.dto.ServiceModuleSandboxDto;
import com.swozo.api.web.sandbox.request.CreateSandboxEnvironmentRequest;
import com.swozo.api.web.servicemodule.ServiceModuleService;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.SandboxMapper;
import com.swozo.model.utils.InstructionDto;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class SandboxService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ThreadPoolTaskScheduler taskScheduler;
    private final CourseService courseService;
    private final UserService userService;
    private final ServiceModuleService serviceModuleService;
    private final ScheduleService scheduleService;
    private final CourseRepository courseRepository;
    private final SandboxValidator sandboxValidator;
    private final SandboxMapper sandboxMapper;

    @Transactional
    public ServiceModuleSandboxDto createServiceModuleTestingEnvironment(
            Long creatorId,
            Long serviceModuleId,
            CreateSandboxEnvironmentRequest request
    ) {
        sandboxValidator.validateCreateSandboxRequest(creatorId, request);
        var serviceModule = serviceModuleService.getById(serviceModuleId);
        var startTime = scheduleService.getAsapScheduleAvailability(serviceModule.getServiceName());
        var validTo = startTime.plusMinutes(request.validForMinutes());

        var sandboxRequest = buildCourseSandboxRequest(creatorId, request.studentCount(), serviceModule, startTime, validTo);
        var sandboxCourseDetails = courseService.createCourse(sandboxRequest, creatorId, true);

        var sandboxUsers = createSandboxUsers(request.studentCount());
        var course = courseRepository.getById(sandboxCourseDetails.id());
        sandboxUsers.forEach(sandboxUser -> courseService.addStudentToCourse(course, sandboxUser.user));

        scheduleSandboxCleanup(startTime, request, sandboxCourseDetails, sandboxUsers);

        return sandboxMapper.toDto(sandboxCourseDetails, sandboxUsers, validTo);
    }

    private CreateCourseRequest buildCourseSandboxRequest(
            Long creatorId,
            int studentCount,
            ServiceModule serviceModule,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        var name = String.format("Sandbox %s - %s - %s", creatorId, serviceModule.getName(), UUID.randomUUID());

        return new CreateCourseRequest(
                name,
                serviceModule.getSubject(),
                serviceModule.getDescription(),
                studentCount,
                false,
                List.of(
                        new CreateActivityRequest(
                                name,
                                serviceModule.getDescription(),
                                startTime,
                                endTime,
                                new InstructionDto(""),
                                List.of(new SelectedServiceModuleDto(serviceModule.getId(), false))
                        )
                ),
                Optional.of(UUID.randomUUID().toString())
        );
    }

    private void scheduleSandboxCleanup(
            LocalDateTime serviceModuleStartTime,
            CreateSandboxEnvironmentRequest request,
            CourseDetailsDto courseDetailsDto,
            List<SandboxUser> users
    ) {
        // TODO check on system startup if some left-over courses failed to be deleted
        var courseCleanupTime = serviceModuleStartTime
                .plusMinutes(request.validForMinutes() + request.resultsValidForMinutes())
                .toInstant(ZoneOffset.UTC);

        logger.info("scheduling sandbox course id: " + courseDetailsDto.id() +  " deletion to " + courseCleanupTime);

        taskScheduler.schedule(
                () -> {
                    try {
                        logger.info("Cleaning up sandbox course: " + courseDetailsDto.id());
                        courseService.deleteCourse(courseDetailsDto.id());
                        userService.removeUsers(users.stream().map(sandboxUser -> sandboxUser.user.getId()).toList());
                        logger.info("Sandbox course: " + courseDetailsDto.id() + " cleaned up successfully");
                    } catch (Throwable ex) {
                        logger.error("Failed to cleanup sandbox course: " + courseDetailsDto.id(), ex);
                    }
                },
                courseCleanupTime
        );
    }

    private List<SandboxUser> createSandboxUsers(int count) {
        return IntStream.rangeClosed(1, count)
                .mapToObj(userNum -> {
                    var plaintextPassword = UUID.randomUUID().toString();
                    var user = userService.createUserInternally(
                            "Sandbox",
                            "Sandbox",
                            String.format("%s@swozo.sandbox.pl", UUID.randomUUID()),
                            plaintextPassword,
                            List.of(RoleDto.STUDENT)
                    );
                    return new SandboxUser(user, plaintextPassword);
                })
                .toList();
    }

    public record SandboxUser(User user, String plaintextPassword) {
    }
}
