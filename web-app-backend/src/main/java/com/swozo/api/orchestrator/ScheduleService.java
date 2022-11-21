package com.swozo.api.orchestrator;

import com.swozo.api.web.activity.ActivityRepository;
import com.swozo.api.web.activitymodule.ActivityScheduleInfoRepository;
import com.swozo.mda.MdaEngine;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.properties.MdaVmSpecs;
import com.swozo.model.scheduling.properties.ServiceDescription;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import com.swozo.model.scheduling.properties.ServiceType;
import com.swozo.persistence.Course;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.activity.ActivityModuleScheduleInfo;
import com.swozo.persistence.activity.UserActivityModuleInfo;
import com.swozo.persistence.mda.models.Psm;
import com.swozo.persistence.mda.vminfo.PsmVmInfo;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.user.User;
import com.swozo.persistence.user.UserCourseData;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.swozo.util.CollectionUtils.iterateSimultaneously;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrchestratorService orchestratorService;
    private final MdaEngine engine;
    private final ActivityRepository activityRepository;
    private final ActivityScheduleInfoRepository activityScheduleInfoRepository;

    public void scheduleActivities(Collection<Activity> activities) {
        var scheduleRequestsWithInfos= activities.stream()
                .flatMap(this::buildScheduleRequestsForActivity)
                .toList();

        var scheduleRequests = scheduleRequestsWithInfos.stream()
                .map(ScheduleRequestWithScheduleInfos::scheduleRequest)
                .toList();

        var responses = orchestratorService.sendScheduleRequests(scheduleRequests);

        iterateSimultaneously(scheduleRequestsWithInfos, responses, this::assignScheduleResponse);
        activityRepository.saveAll(activities);
    }

    public void addStudentToAlreadyScheduledActivities(Course course, User student) {
        if (course.getStudents().size() > course.getExpectedStudentCount()) {
            tryAddingStudentWhoExceededCourseCapacity(course, student);
            return;
        }

        var scheduleInfos = course.getActivities().stream()
                .filter(activity -> activity.getStartTime().isAfter(LocalDateTime.now()))
                .flatMap(futureActivity -> futureActivity.getModules().stream())
                .map(activityModule -> activityModule.getSchedules().stream()
                        .filter(this::canAcceptNewStudent)
                        .findAny()
                        .orElseThrow()
                )
                .toList();

        scheduleInfos.forEach(scheduleInfo -> assignEmptyLinkToUser(scheduleInfo, student));
        activityScheduleInfoRepository.saveAll(scheduleInfos);
    }

    public LocalDateTime getAsapScheduleAvailability(String serviceName) {
        return orchestratorService.getEstimatedAsapServiceAvailability(serviceName);
    }

    private Stream<ScheduleRequestWithScheduleInfos> buildScheduleRequestsForActivity(Activity activity) {
        var psm = engine.processCim(activity.getCourse(), activity);
        return Stream.concat(
                handleTeacherScheduling(psm, activity),
                handleStudentScheduling(psm, activity)
            );
    }

    private Stream<ScheduleRequestWithScheduleInfos> handleTeacherScheduling(Psm psm, Activity activity) {
        var teacherServiceDescriptions = new LinkedList<ServiceDescription>();
        var teacherScheduleInfos = new LinkedList<ActivityModuleScheduleInfo>();

        for (var serviceModule: psm.getTeacherVm().getServiceModules()) {
            var activityModule = findCorrespondingActivityModule(activity, serviceModule);
            var scheduleInfo = buildScheduleInfoWithoutScheduleRequest(activityModule);
            teacherScheduleInfos.add(scheduleInfo);

            teacherServiceDescriptions.push(buildServiceDescription(activityModule, serviceModule));

            assignEmptyLinkToUser(scheduleInfo, activity.getTeacher());

            if (!serviceModule.isIsolated()) {
                assignLinksToStudents(activity, scheduleInfo);
             }
        }

        return Stream.of(buildScheduleRequest(
                teacherScheduleInfos,
                psm.getTeacherVm(),
                provideServiceLifespan(activity),
                teacherServiceDescriptions
        ));
    }

    private Stream<ScheduleRequestWithScheduleInfos> handleStudentScheduling(Psm psm, Activity activity) {
        return psm.getStudentsVms().stream().flatMap(studentPsmInfo -> {
            var alreadyJoinedStudentsIterator = activity.getCourse().getStudents().stream()
                    .map(UserCourseData::getUser)
                    .iterator();

            return IntStream.rangeClosed(1, studentPsmInfo.getAmount())
                    .mapToObj(studentIdx -> Optional.ofNullable(alreadyJoinedStudentsIterator.hasNext() ?
                                    alreadyJoinedStudentsIterator.next() : null)
                    )
                    .flatMap(studentOpt -> createScheduleRequestsForStudent(studentOpt, studentPsmInfo, activity));
        });
    }

    private Stream<ScheduleRequestWithScheduleInfos> createScheduleRequestsForStudent(
            Optional<User> student,
            PsmVmInfo studentPsmInfo,
            Activity activity
    ) {
        var serviceDescriptions = new LinkedList<ServiceDescription>();
        var scheduleInfos = new LinkedList<ActivityModuleScheduleInfo>();

        for (var serviceModule: studentPsmInfo.getServiceModules()) {
            var activityModule = findCorrespondingActivityModule(activity, serviceModule);
            var scheduleInfo = buildScheduleInfoWithoutScheduleRequest(activityModule);
            scheduleInfos.add(scheduleInfo);

            serviceDescriptions.push(buildServiceDescription(activityModule, serviceModule));
            student.ifPresent(presentStudent -> assignEmptyLinkToUser(scheduleInfo, presentStudent));
        }

        return Stream.of(buildScheduleRequest(
                scheduleInfos,
                studentPsmInfo,
                provideServiceLifespan(activity),
                serviceDescriptions
        ));
    }

    private void assignScheduleResponse(ScheduleRequestWithScheduleInfos scheduleRequestData, ScheduleResponse scheduleResponse) {
        var scheduleRequestId = scheduleResponse.requestId();
        scheduleRequestData.scheduleInfos()
                .forEach(scheduleInfo -> scheduleInfo.setScheduleRequestId(scheduleRequestId));
    }

    private boolean canAcceptNewStudent(ActivityModuleScheduleInfo scheduleInfo) {
        return scheduleInfo.getUserActivityModuleData().isEmpty() ||
                !scheduleInfo.getActivityModule().getServiceModule().isIsolated();
    }

    private void tryAddingStudentWhoExceededCourseCapacity(Course course, User student) {
        // xD maybe one day
        logger.warn("User {} exceeded expected course student count for course: {}", student, course.getId());
    }

    private ServiceLifespan provideServiceLifespan(Activity activity) {
        return new ServiceLifespan(activity.getStartTime(), activity.getEndTime());
    }

    private ScheduleRequestWithScheduleInfos buildScheduleRequest(
            List<ActivityModuleScheduleInfo> scheduleInfos,
            PsmVmInfo psmVmInfo,
            ServiceLifespan serviceLifespan,
            List<ServiceDescription> serviceDescriptions
    ) {
       return new ScheduleRequestWithScheduleInfos(
               new ScheduleRequest(
                       serviceLifespan,
                       new MdaVmSpecs(psmVmInfo.getMachineType(), psmVmInfo.getDisk()),
                       serviceDescriptions
               ),
               scheduleInfos
       );
    }

    private ServiceDescription buildServiceDescription(ActivityModule activityModule, ServiceModule serviceModule) {
        return new ServiceDescription(
                activityModule.getId(),
                ServiceType.valueOf(serviceModule.getServiceName()),
                serviceModule.getDynamicProperties()
        );
    }

    private void assignEmptyLinkToUser(ActivityModuleScheduleInfo scheduleInfo, User user) {
        var link = new UserActivityModuleInfo();
        link.setUser(user);
        scheduleInfo.addUserActivityLink(link);
    }

    private void assignLinksToStudents(Activity activity, ActivityModuleScheduleInfo scheduleInfo) {
        activity.getCourse().getStudents().stream()
                .map(UserCourseData::getUser)
                .forEach(student -> assignEmptyLinkToUser(scheduleInfo, student));
    }

    private ActivityModule findCorrespondingActivityModule(Activity activity, ServiceModule serviceModule) {
        return activity.getModules().stream()
                .filter(activityModule -> activityModule.getServiceModule().equals(serviceModule))
                .findAny().orElseThrow();
    }

    private ActivityModuleScheduleInfo buildScheduleInfoWithoutScheduleRequest(ActivityModule activityModule) {
        var scheduleInfo = new ActivityModuleScheduleInfo();
        activityModule.addScheduleInfo(scheduleInfo);
        return scheduleInfo;
    }

    private record ScheduleRequestWithScheduleInfos(
            ScheduleRequest scheduleRequest,
            List<ActivityModuleScheduleInfo> scheduleInfos) {
    }
}
