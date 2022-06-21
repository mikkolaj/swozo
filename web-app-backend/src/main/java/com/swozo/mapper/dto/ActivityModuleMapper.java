package com.swozo.mapper.dto;

import com.swozo.databasemodel.ActivityModule;
import com.swozo.dto.activity.ActivityLinkInfo;
import com.swozo.dto.activitymodule.ActivityModuleDetailsReq;
import com.swozo.dto.activitymodule.ActivityModuleDetailsResp;
import org.mapstruct.Mapper;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class ActivityModuleMapper {
    @Autowired
    private ServiceModuleMapper serviceModuleMapper;

    //    TODO add proper mapping in both functions
    public ActivityModule toPersistence(ActivityModuleDetailsReq activityModuleDetailsReq) {
        return new ActivityModule();
    }

    public ActivityModuleDetailsResp toModel(ActivityModule activityModule) {
        // TODO
        return new ActivityModuleDetailsResp(
                activityModule.getId(),
                serviceModuleMapper.toModel(activityModule.getModule()),
                activityModule.getInstruction(),
                activityModule.getLinks().stream().map(link ->
                        new ActivityLinkInfo(
                                link.getLink(),
                                activityModule.getModule().getScheduleType().toString(),
                                activityModule.getInstruction(),
                                link.getDescription()
                        )).toList());
    }
}

