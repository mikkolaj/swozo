package com.swozo.orchestrator.api.scheduling.control.helpers;


import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;

public record ScheduleRequestWithServiceDescription(
        ScheduleRequestEntity request,
        ServiceDescriptionEntity description
) {
}
