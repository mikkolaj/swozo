package com.swozo.orchestrator.api.scheduling.control;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.model.scheduling.properties.ServiceLifespan;

public record ScheduleRequestWithId(ScheduleRequest request, long requestId) {
    public Psm getPsm() {
        return request.getPsm();
    }

    public ScheduleType getScheduleType() {
        return request.getScheduleType();
    }

    public ServiceLifespan getServiceLifespan() {
        return request.getServiceLifespan();
    }
}
