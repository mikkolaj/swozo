package com.swozo.model.scheduling;

import com.swozo.model.scheduling.properties.ScheduleType;

import java.util.List;

public record ServiceConfig(ScheduleType scheduleType, List<ParameterDescription> parameterDescriptions) {
}
