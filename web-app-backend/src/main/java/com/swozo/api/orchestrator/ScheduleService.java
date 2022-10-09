package com.swozo.api.orchestrator;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import com.swozo.persistence.Activity;
import com.swozo.persistence.ActivityModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

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

    private Psm providePsm(Activity activity) {
        return new Psm("e2-medium", 10);
    }

    private ServiceLifespan provideServiceLifespan(Activity activity) {
        return new ServiceLifespan(activity.getStartTime(), activity.getEndTime());
    }

    private ScheduleRequest buildScheduleServiceRequest(ActivityWithModule activityWithModule) {
        var hardcodedParameters = new HashMap<String, String>();
        hardcodedParameters.put("notebookLocation", "somewhereOverTheRainbow");
        return new ScheduleRequest(
                provideServiceLifespan(activityWithModule.activity()),
                providePsm(activityWithModule.activity()),
                ScheduleType.JUPYTER,
                hardcodedParameters
        );
    }

    private record ActivityWithModule(Activity activity, ActivityModule module) {
    }
}
