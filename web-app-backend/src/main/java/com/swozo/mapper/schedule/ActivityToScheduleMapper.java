package com.swozo.mapper.schedule;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityModule;
import com.swozo.model.scheduling.JupyterScheduleRequest;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;

@Component
public class ActivityToScheduleMapper {

    private ScheduleRequest createJupyter(ActivityModule activityModule, LocalDateTime startTime, LocalDateTime endTime) {
        return new JupyterScheduleRequest(
                "/tmp",
                new ServiceLifespan(startTime, endTime),
                new Psm("e2-medium", 10),
                activityModule.getId());
    }

    public Collection<ScheduleRequest> getScheduleReqsFromActivity(Activity activity) {
////        to change while adding new schedule requests - choose which schedule creation method use based on schedule type
        Collection<ScheduleRequest> schedules = new LinkedList<>();
        for (ActivityModule activityModule : activity.getModules()) {
            schedules.add(createJupyter(activityModule, activity.getStartTime(), activity.getEndTime()));
        }
        return schedules;
    }
}
