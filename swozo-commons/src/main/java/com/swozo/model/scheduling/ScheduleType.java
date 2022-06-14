package com.swozo.model.scheduling;

import lombok.Getter;

@Getter
public enum ScheduleType {
    JUPYTER(ScheduleJupyter.class);

    private final Class<?>  scheduleRequestClass;

    ScheduleType(Class<?>  scheduleRequestClass) {
        this.scheduleRequestClass = scheduleRequestClass;
    }
}
