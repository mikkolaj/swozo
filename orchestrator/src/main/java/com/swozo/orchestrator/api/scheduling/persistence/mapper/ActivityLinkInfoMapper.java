package com.swozo.orchestrator.api.scheduling.persistence.mapper;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ActivityLinkInfoEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ActivityLinkInfoMapper {
    default ActivityLinkInfoEntity toPersistence(ActivityLinkInfo request, ScheduleRequestEntity requestEntity) {
        return new ActivityLinkInfoEntity(request.url(), request.connectionInfo(), requestEntity);
    }

    ActivityLinkInfo toDto(ActivityLinkInfoEntity request);
}
