package com.swozo.model.scheduling;

import com.swozo.model.Psm;
import com.swozo.model.ServiceLifespan;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public final class ScheduleJupyter extends ScheduleRequest {
    private final String noteBookLocation;

    public ScheduleJupyter(String notebookLocation, ServiceLifespan serviceLifespan, Psm psm, Long activityModuleID) {
        super(serviceLifespan, psm, activityModuleID);
        this.noteBookLocation = notebookLocation;
    }
}
