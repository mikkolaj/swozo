package com.swozo.mapper;

import com.swozo.api.web.activity.dto.ServiceConnectionDetailsDto;
import com.swozo.api.web.activitymodule.dto.ActivityModuleDetailsDto;
import com.swozo.model.utils.InstructionDto;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.activity.UserActivityModuleInfo;
import com.swozo.persistence.servicemodule.ServiceModule;
import com.swozo.persistence.user.User;
import com.swozo.utils.SupportedLanguage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ActivityModuleMapper {
    @Autowired
    protected ServiceModuleMapper serviceModuleMapper;
    @Autowired
    protected CommonMappers commonMappers;

    public ActivityModule fromServiceModule(ServiceModule serviceModule) {
        return new ActivityModule(serviceModule);
    }

    protected Map<SupportedLanguage, InstructionDto> instructionsToDto(UserActivityModuleInfo activityLink) {
        return activityLink.getTranslations().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> commonMappers.instructionToDto(entry.getValue().getInstructionHtml())
                ));
    }

    protected List<ServiceConnectionDetailsDto> connectionDetailsToDto(ActivityModule activityModule, User user) {
        return activityModule.getSchedules().stream()
                .flatMap(scheduleInfo -> scheduleInfo.getUserActivityModuleData().stream())
                .filter(userActivityLink -> userActivityLink.getUser().equals(user) && userActivityLink.getUrl().isPresent())
                .map(userActivityLink -> new ServiceConnectionDetailsDto(
                        instructionsToDto(userActivityLink),
                        userActivityLink.getUrl().get())
                )
                .toList();
    }

    @Mapping(target = "id", source = "activityModule.id")
    @Mapping(target = "serviceModule", expression = "java(serviceModuleMapper.toSummaryDto(activityModule.getServiceModule()))")
    @Mapping(target = "connectionDetails", expression = "java(connectionDetailsToDto(activityModule, user))")
    public abstract ActivityModuleDetailsDto toDto(ActivityModule activityModule, User user);
}

