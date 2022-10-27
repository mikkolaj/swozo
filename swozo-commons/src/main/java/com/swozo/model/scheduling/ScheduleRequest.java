package com.swozo.model.scheduling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.model.scheduling.properties.ServiceLifespan;

import java.time.format.DateTimeFormatter;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScheduleRequest(
        ServiceLifespan serviceLifespan,
        Psm psm,
        ScheduleType scheduleType,
        Map<String, String> dynamicProperties
) {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-HH-mm-ss");

    public String buildVmNamePrefix() {
        return String.format("%s---%s---%s",
                scheduleType.toString().toLowerCase(),
                serviceLifespan.startTime().format(formatter),
                serviceLifespan.endTime().format(formatter)
        );
    }
}
