package com.swozo.mapper;

import com.swozo.api.web.activity.dto.ServiceConnectionDetailsDto;
import com.swozo.api.web.activitymodule.dto.ActivityModuleDetailsDto;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.persistence.ActivityLink;
import com.swozo.persistence.ActivityModule;
import com.swozo.persistence.ServiceModule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ActivityModuleMapper {

    @Autowired
    protected ServiceModuleMapper serviceModuleMapper;

    public abstract ActivityLink toPersistence(ActivityLinkInfo activityLinkInfo);

    public ActivityModule fromServiceModule(ServiceModule serviceModule) {
        var activityModule = new ActivityModule(serviceModule);
        activityModule.setInstruction("TODO");
        return activityModule;
    }

    @Mapping(target = "serviceName", expression = "java(activityModule.getModule().getScheduleTypeName())")
    @Mapping(target = "connectionInstruction", source = "activityModule.instruction")
    @Mapping(target = "url", expression = "java(Optional.ofNullable(activityLink.getUrl()))")
    @Mapping(target = "connectionInfo", expression = "java(Optional.ofNullable(activityLink.getConnectionInfo()))")
    public abstract ServiceConnectionDetailsDto toDto(ActivityLink activityLink, ActivityModule activityModule);

    @Mapping(target = "module", expression = "java(serviceModuleMapper.toDto(activityModule.getModule()))")
    @Mapping(target = "connectionDetails", expression = "java(activityModule.getLinks().stream().map(link -> toDto(link, activityModule)).toList())")
    public abstract ActivityModuleDetailsDto toDto(ActivityModule activityModule);
}

