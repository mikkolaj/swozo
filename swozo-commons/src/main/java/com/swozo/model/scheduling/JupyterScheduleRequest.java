package com.swozo.model.scheduling;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.model.scheduling.properties.ServiceLifespan;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public final class JupyterScheduleRequest extends ScheduleRequest {
    private final String notebookLocation;

    @JsonCreator
    public JupyterScheduleRequest(@JsonProperty("notebookLocation") String notebookLocation,
                                  @JsonProperty("serviceLifespan") ServiceLifespan serviceLifespan,
                                  @JsonProperty("psm") Psm psm
    ) {
        super(serviceLifespan, psm, ScheduleType.JUPYTER);
        this.notebookLocation = notebookLocation;
    }
}
