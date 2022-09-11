package com.swozo.orchestrator.api.scheduling;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.scheduling.ScheduleRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ScheduleRequestTracker {
    // TODO: switch to storing request info in database
    private final Map<Long, RequestWithLinks> requestDb = new HashMap<>();

    public void persist(ScheduleRequest scheduleRequest) {
        requestDb.put(scheduleRequest.getActivityModuleID(), new RequestWithLinks(scheduleRequest, Collections.emptyList()));
    }

    public void unpersist(Long activityModuleId) {
        requestDb.remove(activityModuleId);
    }

    public List<ActivityLinkInfo> getLinks(Long activityModuleId) {
        return Optional.ofNullable(requestDb.get(activityModuleId))
                .map(RequestWithLinks::links)
                .orElse(Collections.emptyList());
    }

    public void saveLinks(Long activityModuleId, List<ActivityLinkInfo> links) {
        Optional.ofNullable(requestDb.get(activityModuleId))
                .ifPresent(requestWithLinks ->
                        requestDb.put(activityModuleId, new RequestWithLinks(requestWithLinks.scheduleRequest, links))
                );
    }

    private record RequestWithLinks(ScheduleRequest scheduleRequest, List<ActivityLinkInfo> links) {
    }
}
