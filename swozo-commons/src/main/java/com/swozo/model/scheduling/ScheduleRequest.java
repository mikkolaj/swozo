package com.swozo.model.scheduling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import lombok.*;

@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
@JsonSubTypes({
        @JsonSubTypes.Type(value = JupyterScheduleRequest.class, name = "ScheduleJupyter")
})
public abstract class ScheduleRequest {
    private final ServiceLifespan serviceLifespan;
    private final Psm psm;
    private final Long activityModuleID;
    private final ScheduleType scheduleType;
}
