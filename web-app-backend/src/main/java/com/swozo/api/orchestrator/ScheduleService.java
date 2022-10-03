package com.swozo.api.orchestrator;

import com.swozo.api.web.activitymodule.ActivityModuleRepository;
import com.swozo.model.scheduling.JupyterScheduleRequest;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.ScheduleResponse;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import com.swozo.persistence.Activity;
import com.swozo.persistence.ActivityModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.function.BiConsumer;

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

        iterateSimultaneously(allActivityModules, requestIds, this::updateActivityModule);
    }

    private <S, T> void iterateSimultaneously(Collection<S> col1, Collection<T> col2, BiConsumer<S, T> consumer) {
        var it1 = col1.iterator();
        var it2 = col2.iterator();
        while (it1.hasNext() && it2.hasNext()) {
            var var1 = it1.next();
            var var2 = it2.next();
            consumer.accept(var1, var2);
        }
    }

    private void updateActivityModule(ActivityModule module, Long requestId) {
        module.setRequestId(requestId);
        moduleRepository.save(module);
    }

    private Psm providePsm(Activity activity) {
        return new Psm("e2-medium", 10);
    }

    private ServiceLifespan provideServiceLifespan(Activity activity) {
        return new ServiceLifespan(activity.getStartTime(), activity.getEndTime());
    }

    private ScheduleRequest buildScheduleServiceRequest(ActivityWithModule activityWithModule) {
        return new JupyterScheduleRequest(
                "TODO",
                provideServiceLifespan(activityWithModule.activity()),
                providePsm(activityWithModule.activity())
        );
    }

    private record ActivityWithModule(Activity activity, ActivityModule module) {
    }
}
