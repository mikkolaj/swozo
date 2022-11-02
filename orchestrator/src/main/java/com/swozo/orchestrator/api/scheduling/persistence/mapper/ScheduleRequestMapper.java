package com.swozo.orchestrator.api.scheduling.persistence.mapper;

import com.swozo.model.scheduling.ScheduleRequest;
import com.swozo.model.scheduling.properties.Psm;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = ServiceTypeMapper.class)
public interface ScheduleRequestMapper {
    ServiceTypeMapper serviceTypeMapper = Mappers.getMapper(ServiceTypeMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "vmResourceId", ignore = true)
    @Mapping(target = "startTime", expression = "java(request.serviceLifespan().startTime())")
    @Mapping(target = "endTime", expression = "java(request.serviceLifespan().endTime())")
    @Mapping(target = "machineType", expression = "java(request.psm().machineType())")
    @Mapping(target = "diskSizeGb", expression = "java(request.psm().diskSizeGb())")
    ScheduleRequestEntity toPersistence(ScheduleRequest request);

    @Mapping(target = "serviceLifespan", expression = "java(new ServiceLifespan(request.getStartTime(), request.getEndTime()))")
    @Mapping(target = "psm", expression = "java(new Psm(request.getMachineType(), request.getDiskSizeGb()))")
    ScheduleRequest toDto(ScheduleRequestEntity request);

    default Psm toPsm(ScheduleRequestEntity requestEntity) {
        return new Psm(requestEntity.getMachineType(), requestEntity.getDiskSizeGb());
    }
}


