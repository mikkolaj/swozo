package com.swozo.orchestrator.api.scheduling.persistence.mapper;

import com.swozo.model.scheduling.properties.ServiceType;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceTypeEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ServiceTypeMapper {

    ServiceTypeEntity toPersistence(ServiceType request);

    ServiceType toDto(ServiceTypeEntity request);
}


