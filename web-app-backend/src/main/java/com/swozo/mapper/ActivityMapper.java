package com.swozo.mapper;

import com.swozo.databasemodel.Activity;
import com.swozo.databasemodel.ActivityInstruction;
import com.swozo.dto.activity.ActivityDetailsDto;
import com.swozo.dto.activity.ActivityInstructionDto;
import com.swozo.dto.activity.CreateActivityRequest;
import com.swozo.webservice.repository.ServiceModuleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ActivityMapper {
    @Autowired
    protected ServiceModuleRepository moduleRepository;
    @Autowired
    protected ActivityModuleMapper activityModuleMapper;

    public abstract ActivityInstruction toPersistence(ActivityInstructionDto activityInstructionDto);

    public abstract ActivityInstructionDto toDto(ActivityInstruction activityInstruction);

    @Mapping(target = "modules", expression = "java(moduleRepository.findAllById(activityDetailsReq.selectedModulesIds()).stream().map(activityModuleMapper::fromServiceModule).toList())")
    @Mapping(target = "instructionsFromTeacher", expression = "java(activityDetailsReq.instructionsFromTeacher().stream().map(this::toPersistence).toList())")
    public abstract Activity toPersistence(CreateActivityRequest createActivityRequest);

    @Mapping(target = "instructionsFromTeacher", expression = "java(activity.getInstructionsFromTeacher().stream().map(this::toModel).toList())")
    @Mapping(target = "activityModules", expression = "java(activity.getModules().stream().map(activityModuleMapper::toModel).toList())")
    public abstract ActivityDetailsDto toDto(Activity activity);
}
