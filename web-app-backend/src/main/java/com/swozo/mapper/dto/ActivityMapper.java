package com.swozo.mapper.dto;

import com.swozo.databasemodel.Activity;
import com.swozo.dto.activity.ActivityDetailsReq;
import com.swozo.dto.activity.ActivityDetailsResp;
import com.swozo.dto.activity.ActivityInstruction;
import com.swozo.webservice.repository.ActivityModuleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ActivityMapper {
    @Autowired
    protected ActivityModuleRepository moduleRepository;
    @Autowired
    protected ActivityModuleMapper activityModuleMapper;

    // TODO replace this when we have proper instructions format in db
    protected String getInstructions(ActivityDetailsReq activityDetailsReq) {
        return activityDetailsReq.instructionsFromTeacher().stream()
                .map(activityInstruction -> activityInstruction.header() + activityInstruction.body())
                .reduce("", (cur, prev) -> prev + cur);
    }

    // TODO store instructions properly
    @Mapping(target = "modules", expression = "java(moduleRepository.findAllById(activityDetailsReq.selectedModulesIds()))")
    @Mapping(target = "instructionsFromTeacher", expression = "java(getInstructions(activityDetailsReq))")
    public abstract Activity toPersistence(ActivityDetailsReq activityDetailsReq);

    // TODO proper mapping
    public ActivityDetailsResp toModel(Activity activity) {
        System.out.println("help me: " + activity.getModules().stream()
                .map(activityModule -> activityModuleMapper.toModel(activityModule)).toList());
        return new ActivityDetailsResp(
                activity.getId(),
                activity.getName(),
                activity.getDescription(),
                activity.getStartTime(),
                activity.getEndTime(),
                List.of(new ActivityInstruction("some header", activity.getInstructionsFromTeacher())),
                activity.getModules().stream()
                        .map(activityModule -> activityModuleMapper.toModel(activityModule)).toList());
    }
}
