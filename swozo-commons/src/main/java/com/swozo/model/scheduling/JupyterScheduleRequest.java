package com.swozo.model.scheduling;

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

    public JupyterScheduleRequest(String notebookLocation, ServiceLifespan serviceLifespan, Psm psm, Long activityModuleID) {
        super(serviceLifespan, psm, activityModuleID, ScheduleType.JUPYTER);
        this.notebookLocation = notebookLocation;
    }
}
