package com.swozo.model.scheduling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.swozo.model.scheduling.properties.MdaVmSpecs;
import com.swozo.model.scheduling.properties.ServiceDescription;
import com.swozo.model.scheduling.properties.ServiceLifespan;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ScheduleRequest(
        ServiceLifespan serviceLifespan,
        MdaVmSpecs mdaVmSpecs,
        List<ServiceDescription> serviceDescriptions
) {
}
