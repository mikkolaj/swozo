package com.swozo.mapper;

import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.persistence.ServiceModule;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ServiceModuleMapper {
    @Autowired
    protected ServiceModuleRepository serviceModuleRepository;

    public abstract ServiceModuleDetailsDto toDto(ServiceModule serviceModule);
}
