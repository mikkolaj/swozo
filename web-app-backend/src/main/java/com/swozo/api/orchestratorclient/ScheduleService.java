package com.swozo.api.orchestratorclient;

import com.swozo.databasemodel.Activity;
import com.swozo.mapper.schedule.ActivityToScheduleMapper;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.webservice.service.ActivityModuleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ScheduleService {
    private final ActivityToScheduleMapper activityToScheduleMapper;
    private final OrchestratorService orchestratorService;
    private final ActivityModuleService activityModuleService;

    public void scheduleActivities(Collection<Activity> activities) {
        for (Activity activity : activities) {

            Collection<ScheduleRequest> schedules = activityToScheduleMapper
                    .getScheduleReqsFromActivity(activity);
//            orchestratorService.postScheduleRequestsList(schedules);
            activityModuleService.provideLinksForActivityModules(activity.getModules());
        }
    }
}
