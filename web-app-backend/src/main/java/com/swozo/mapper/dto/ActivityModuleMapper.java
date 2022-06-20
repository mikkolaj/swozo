package com.swozo.mapper.dto;

import com.swozo.databasemodel.ActivityModule;
import com.swozo.dto.activitymodule.ActivityModuleDetailsReq;
import com.swozo.dto.activitymodule.ActivityModuleDetailsResp;
import com.swozo.webservice.service.ActivityModuleService;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;


@Mapper(componentModel = "spring")
public abstract class ActivityModuleMapper {
    @Autowired
    protected ActivityModuleService activityModuleService;
    @Autowired
    private ServiceModuleMapper serviceModuleMapper;

    //    TODO add proper mapping in both functions (actually we don't use ActivityModuleDetailReq anywhere)
    public ActivityModule toPersistence(ActivityModuleDetailsReq activityModuleDetailsReq) {
        return new ActivityModule();
    }

    public ActivityModuleDetailsResp toModel(ActivityModule activityModule) {
        return new ActivityModuleDetailsResp(
                activityModule.getId(),
                serviceModuleMapper.toModel(activityModule.getModule()),
                activityModule.getInstruction(),
                activityModule.getLinks().stream().toList());
    }
}

