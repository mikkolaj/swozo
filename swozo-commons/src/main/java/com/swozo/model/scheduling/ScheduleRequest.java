package com.swozo.model.scheduling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.model.scheduling.properties.ServiceLifespan;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScheduleRequest(
        ServiceLifespan serviceLifespan,
        Psm psm,
        ScheduleType scheduleType,
        String scheduleVersion,
        Map<String, String> dynamicProperties
) {
}
