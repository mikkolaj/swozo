package com.swozo.mapper;

import com.swozo.databasemodel.ActivityModule;
import com.swozo.dto.activitymodule.ActivityModuleDetailsReq;
import com.swozo.dto.activitymodule.ActivityModuleDetailsResp;
import com.swozo.webservice.service.ActivityModuleService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.LinkedList;

@Mapper(componentModel = "spring")
public abstract class ActivityModuleMapper {
    @Autowired
    protected ActivityModuleService activityModuleService;
    private ServiceModuleMapper serviceModuleMapper;
    private ActivityMapper activityMapper;

//    TODO add proper mapping in both functions
    public ActivityModule toPersistence(ActivityModuleDetailsReq activityModuleDetailsReq){
        return new ActivityModule();
    }

    public ActivityModuleDetailsResp toModel(ActivityModule activityModule){
        return new ActivityModuleDetailsResp(
                activityModule.getId(),
                serviceModuleMapper.toModel(activityModule.getModule()),
                activityMapper.toModel(activityModule.getActivity()),
                activityModule.getInstruction(),
                new LinkedList<>());
    }
}

