package com.swozo.mapper;

import com.swozo.databasemodel.ServiceModule;
import com.swozo.dto.servicemodule.ServiceModuleDetailsReq;
import com.swozo.dto.servicemodule.ServiceModuleDetailsResp;
import com.swozo.webservice.repository.ServiceModuleRepository;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ServiceModuleMapper {
    @Autowired
    protected ServiceModuleRepository serviceModuleRepository;

    public abstract ServiceModule toPersistence(ServiceModuleDetailsReq serviceModuleDetailsReq);

    public abstract ServiceModuleDetailsResp toModel(ServiceModule serviceModule);
}
