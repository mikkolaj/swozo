package com.swozo.mapper;

import com.swozo.api.web.activity.dto.ActivityDetailsDto;
import com.swozo.api.web.activity.dto.ActivityInstructionDto;
import com.swozo.api.web.activity.request.CreateActivityRequest;
import com.swozo.api.web.servicemodule.ServiceModuleRepository;
import com.swozo.persistence.Activity;
import com.swozo.persistence.ActivityInstruction;
import com.swozo.persistence.ActivityModule;
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

    public abstract ActivityInstruction toPersistence(ActivityInstructionDto activityInstructionDto);

    public abstract ActivityInstructionDto toDto(ActivityInstruction activityInstruction);

    protected LinkedList<ActivityModule> modulesToPersistence(CreateActivityRequest createActivityRequest) {
        return moduleRepository.findAllById(createActivityRequest.selectedModulesIds()).stream()
                .map(activityModuleMapper::fromServiceModule).collect(Collectors.toCollection(LinkedList::new));
    }

    protected LinkedList<ActivityInstruction> instructionsToPersistence(CreateActivityRequest createActivityRequest) {
        return createActivityRequest.instructionsFromTeacher().stream()
                .map(this::toPersistence).collect(Collectors.toCollection(LinkedList::new));
    }

    @Mapping(target = "modules", expression = "java(modulesToPersistence(createActivityRequest))")
    @Mapping(target = "instructionsFromTeacher", expression = "java(instructionsToPersistence(createActivityRequest))")
    public abstract Activity toPersistence(CreateActivityRequest createActivityRequest);

    @Mapping(target = "instructionsFromTeacher", expression = "java(activity.getInstructionsFromTeacher().stream().map(this::toDto).toList())")
    @Mapping(target = "activityModules", expression = "java(activity.getModules().stream().map(activityModuleMapper::toDto).toList())")
    public abstract ActivityDetailsDto toDto(Activity activity);
}
