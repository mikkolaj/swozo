package com.swozo.orchestrator.api.scheduling.persistence.mapper;

import com.swozo.model.scheduling.JupyterScheduleRequest;
import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.orchestrator.api.scheduling.persistence.entity.JupyterScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleRequestMapper {
    default ScheduleRequestEntity toPersistence(ScheduleRequest scheduleRequest) {
        return switch (scheduleRequest) {
            case JupyterScheduleRequest jupyterScheduleRequest -> toPersistence(jupyterScheduleRequest);
        };
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vmResourceId", ignore = true)
    @Mapping(target = "startTime", expression = "java(request.getServiceLifespan().startTime())")
    @Mapping(target = "endTime", expression = "java(request.getServiceLifespan().endTime())")
    @Mapping(target = "machineType", expression = "java(request.getPsm().machineType())")
    @Mapping(target = "diskSizeGb", expression = "java(request.getPsm().diskSizeGb())")
    JupyterScheduleRequestEntity toPersistence(JupyterScheduleRequest request);

    @Mapping(target = "serviceLifespan", expression = "java(new ServiceLifespan(request.getStartTime(), request.getEndTime()))")
    @Mapping(target = "psm", expression = "java(new Psm(request.getMachineType(), request.getDiskSizeGb()))")
    JupyterScheduleRequest toDto(JupyterScheduleRequestEntity request);
}


