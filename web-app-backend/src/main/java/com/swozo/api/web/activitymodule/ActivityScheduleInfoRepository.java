package com.swozo.api.web.activitymodule;

import com.swozo.persistence.activity.ActivityModuleScheduleInfo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ActivityScheduleInfoRepository extends JpaRepository<ActivityModuleScheduleInfo, Long> {
    List<ActivityModuleScheduleInfo> getAllByScheduleRequestIdAndActivityModule_ServiceModuleId(Long scheduleRequestId, Long serviceModuleId);

    default List<Long> getUserIdsByScheduleRequestIdAndActivityModule_ServiceModuleId(Long scheduleRequestId, Long serviceModuleId) {
        // TODO just for demonstration that it should be possible
        return getAllByScheduleRequestIdAndActivityModule_ServiceModuleId(scheduleRequestId, serviceModuleId).stream()
                .map(ActivityModuleScheduleInfo::getUserActivityLinks)
                .flatMap(Collection::stream)
                .map(userActivityLink -> userActivityLink.getUser().getId())
                .toList();
    }

}
