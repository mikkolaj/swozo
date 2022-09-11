package com.swozo.orchestrator.api.links;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.orchestrator.api.scheduling.ScheduleRequestTracker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final ScheduleRequestTracker requestTracker;

    public List<ActivityLinkInfo> getLinks(Long activityModuleId) {
        return requestTracker.getLinks(activityModuleId);
    }
}
