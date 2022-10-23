package com.swozo.api.orchestrator;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

import static com.swozo.util.CollectionUtils.iterateSimultaneously;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final OrchestratorService orchestratorService;
    private final ActivityModuleRepository moduleRepository;

    public void scheduleActivities(Collection<Activity> activities) {
        var activitiesWithModules = activities
                .stream()
                .flatMap(activity -> activity.getModules().stream().map(module -> new ActivityWithModule(activity, module)))
                .toList();

        var scheduleRequests = activitiesWithModules.stream()
                .map(this::buildScheduleServiceRequest)
                .toList();

        var requestIds = orchestratorService.sendScheduleRequests(scheduleRequests).stream()
                .map(ScheduleResponse::requestId)
                .toList();

        var allActivityModules = activitiesWithModules.stream()
                .map(ActivityWithModule::module)
                .toList();

        iterateSimultaneously(allActivityModules, requestIds, ActivityModule::setRequestId);
        moduleRepository.saveAll(allActivityModules);
    }

    public LocalDateTime getAsapScheduleAvailability() {
        // TODO don't hardcode this
        return LocalDateTime.now().plusMinutes(10);
    }

    private Psm providePsm(Activity activity) {
        return new Psm("e2-medium", 10);
    }

    private ServiceLifespan provideServiceLifespan(Activity activity) {
        return new ServiceLifespan(activity.getStartTime(), activity.getEndTime());
    }

    private ScheduleRequest buildScheduleServiceRequest(ActivityWithModule activityWithModule) {
        return new ScheduleRequest(
                provideServiceLifespan(activityWithModule.activity()),
                providePsm(activityWithModule.activity()),
                ScheduleType.JUPYTER,
                "1.0.0",
                activityWithModule.module.getServiceModule().getDynamicProperties()
        );
    }

    private record ActivityWithModule(Activity activity, ActivityModule module) {
    }
}
