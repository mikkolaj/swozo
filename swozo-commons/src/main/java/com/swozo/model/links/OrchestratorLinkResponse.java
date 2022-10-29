package com.swozo.model.links;

import java.util.List;

public record OrchestratorLinkResponse(
        Long scheduleRequestId,
        List<ActivityLinkInfo> links
) {
}
