package com.swozo.orchestrator.api.scheduling.persistence.mapper;

import com.swozo.model.scheduling.properties.ScheduleType;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ScheduleTypeMapper {

    ScheduleTypeEntity toPersistence(ScheduleType request);

    ScheduleType toDto(ScheduleTypeEntity request);
}


