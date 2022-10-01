package com.swozo.orchestrator.api.scheduling.persistence.mapper;

import com.swozo.model.links.ActivityLinkInfo;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ActivityLinkInfoEntity;
import com.swozo.orchestrator.api.scheduling.persistence.repository.ScheduleRequestRepository;
import lombok.RequiredArgsConstructor;
import org.mapstruct.Mapper;

@RequiredArgsConstructor
@Mapper(componentModel = "spring")
public abstract class ActivityLinkInfoMapper {
    private ScheduleRequestRepository scheduleRequestRepository;

    public ActivityLinkInfoEntity toPersistence(ActivityLinkInfo request, long scheduleRequestId) {
        return new ActivityLinkInfoEntity(request.url(), request.connectionInfo(), scheduleRequestRepository.getById(scheduleRequestId));
    }

    public abstract ActivityLinkInfo toDto(ActivityLinkInfoEntity request);
}
