package com.swozo.api.orchestrator;

import com.swozo.api.web.activity.ActivityRepository;
import com.swozo.mda.MdaEngine;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.properties.MdaVmSpecs;
import com.swozo.model.scheduling.properties.ServiceDescription;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import com.swozo.model.scheduling.properties.ServiceType;
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
    private final OrchestratorService orchestratorService;
    private final MdaEngine engine;
    private final ActivityRepository activityRepository;

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

            assignLinkToUser(scheduleInfo, activity.getCourse().getTeacher());

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
        var studentPsmInfoOpt = psm.getStudentsVms();
        if (studentPsmInfoOpt.isEmpty()) {
            return Stream.of();
        }
        var studentPsmInfo = studentPsmInfoOpt.get();

        var alreadyJoinedStudentsIterator = activity.getCourse().getStudents().stream()
                .map(UserCourseData::getUser)
                .iterator();

        return IntStream.rangeClosed(1, studentPsmInfo.getAmount())
                .mapToObj(studentIdx ->
                    Optional.ofNullable(alreadyJoinedStudentsIterator.hasNext() ? alreadyJoinedStudentsIterator.next() : null)
                )
                .flatMap(studentOpt -> createScheduleRequestsForStudent(studentOpt, studentPsmInfo, activity));
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
            student.ifPresent(presentStudent -> assignLinkToUser(scheduleInfo, presentStudent));
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

    public LocalDateTime getAsapScheduleAvailability() {
        // TODO don't hardcode this
        return LocalDateTime.now().plusMinutes(10);
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

    private void assignLinkToUser(ActivityModuleScheduleInfo scheduleInfo, User user) {
        var link = new UserActivityModuleInfo();
        link.setUser(user);
        scheduleInfo.addUserActivityLink(link);
    }

    private void assignLinksToStudents(Activity activity, ActivityModuleScheduleInfo scheduleInfo) {
        activity.getCourse().getStudents().stream()
                .map(UserCourseData::getUser)
                .forEach(student -> assignLinkToUser(scheduleInfo, student));
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
