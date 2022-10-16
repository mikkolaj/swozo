package com.swozo.mapper;

import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.servicemodule.dto.ServiceConfigDto;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.model.scheduling.ServiceConfig;
import com.swozo.persistence.ServiceModule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ServiceModuleMapper {
    @Autowired
    protected ServiceModuleRepository serviceModuleRepository;

    public abstract ServiceModuleDetailsDto toDto(ServiceModule serviceModule);

    @Mapping(target = "serviceName", expression = "java(serviceConfig.scheduleType().toString())")
    public abstract ServiceConfigDto toDto(ServiceConfig serviceConfig);
}
