package com.swozo.api.orchestratorclient;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.model.scheduling.JupyterScheduleRequest;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final OrchestratorService orchestratorService;

    public void scheduleActivities(Collection<Activity> activities) {
        for (Activity activity : activities) {
            var scheduleRequests = activity.getModules().stream()
                    .map(activityModule -> buildScheduleServiceRequest(activity, activityModule))
                    .toList();

            // TODO aggregate scheduleRequests for every activity in one POST
            // both for communication performance and maybe for better scheduling possibilities
            orchestratorService.postScheduleRequestsList(scheduleRequests);
        }
    }

    private Psm providePsm(Activity activity) {
        return new Psm("e2-medium", 10);
    }

    private ServiceLifespan provideServiceLifespan(Activity activity) {
        return new ServiceLifespan(activity.getStartTime(), activity.getEndTime());
    }

    private ScheduleRequest buildScheduleServiceRequest(Activity activity, ActivityModule activityModule) {
        return new JupyterScheduleRequest(
                "TODO",
                provideServiceLifespan(activity),
                providePsm(activity),
                activityModule.getId()
        );
    }

}
