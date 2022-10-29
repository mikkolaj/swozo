package com.swozo.mapper;

import com.swozo.api.web.activity.dto.ServiceConnectionDetailsDto;
import com.swozo.api.web.activitymodule.dto.ActivityModuleDetailsDto;
import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.model.utils.InstructionDto;
import com.swozo.persistence.ServiceModule;
import com.swozo.persistence.activity.ActivityLink;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.activity.utils.TranslatableActivityLink;
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

    protected abstract ActivityLink toBasicPersistence(ActivityLinkInfo activityLinkInfo);

    public ActivityLink toPersistence(ActivityLinkInfo activityLinkInfo) {
        var activityLink = toBasicPersistence(activityLinkInfo);
        activityLinkInfo.connectionInstructionHtml()
                .forEach((language, value) -> activityLink.setTranslation(new TranslatableActivityLink(language, value)));

        return activityLink;
    }

    public ActivityModule fromServiceModule(ServiceModule serviceModule) {
        return new ActivityModule(serviceModule);
    }

    protected Map<SupportedLanguage, InstructionDto> instructionsToDto(ActivityLink activityLink) {
        return activityLink.getTranslations().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> commonMappers.instructionToDto(entry.getValue().getInstructionHtml())
                ));
    }

    protected List<ServiceConnectionDetailsDto> connectionDetailsToDto(ActivityModule activityModule) {
        return activityModule.getLinks().stream().map(this::toDto).toList();
    }

    @Mapping(target = "connectionInstructions", expression = "java(instructionsToDto(activityLink))")
    protected abstract ServiceConnectionDetailsDto toDto(ActivityLink activityLink);

    @Mapping(target = "serviceModule", expression = "java(serviceModuleMapper.toSummaryDto(activityModule.getServiceModule()))")
    @Mapping(target = "connectionDetails", expression = "java(connectionDetailsToDto(activityModule))")
    public abstract ActivityModuleDetailsDto toDto(ActivityModule activityModule);
}

