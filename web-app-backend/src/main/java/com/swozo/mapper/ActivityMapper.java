package com.swozo.mapper;

import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.activity.request.CreateActivityRequest;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class ActivityMapper {
    @Autowired
    protected ServiceModuleRepository moduleRepository;
    @Autowired
    protected ActivityModuleMapper activityModuleMapper;
    @Autowired
    protected FileMapper fileMapper;
    @Autowired
    protected CommonMappers commonMappers;

    protected LinkedList<ActivityModule> modulesToPersistence(CreateActivityRequest createActivityRequest) {
        return moduleRepository.findAllById(createActivityRequest.selectedModulesIds()).stream()
                .map(activityModuleMapper::fromServiceModule).collect(Collectors.toCollection(LinkedList::new));
    }

    @Mapping(target = "modules", expression = "java(modulesToPersistence(createActivityRequest))")
    @Mapping(target = "instructionFromTeacherHtml", expression = "java(commonMappers.instructionToPersistence(createActivityRequest.instructionFromTeacher()))")
    protected abstract Activity basicToPersistence(CreateActivityRequest createActivityRequest);

    public Activity toPersistence(CreateActivityRequest createActivityRequest) {
        var activity = basicToPersistence(createActivityRequest);
        activity.getModules().forEach(module -> module.setActivity(activity));
        return activity;
    }

    @Mapping(target = "instructionFromTeacher", expression = "java(commonMappers.instructionToDto(activity.getInstructionFromTeacherHtml()))")
    @Mapping(target = "activityModules", expression = "java(activity.getModules().stream().map(activityModuleMapper::toDto).toList())")
    @Mapping(target = "publicFiles", expression = "java(activity.getPublicFiles().stream().map(fileMapper::toDto).toList())")
    public abstract ActivityDetailsDto toDto(Activity activity);

    @Mapping(target = "selectedModulesIds", expression = "java(activity.getModules().stream().map(module -> module.getId()).toList())")
    public abstract CreateActivityRequest toRequest(Activity activity);
}
