package com.swozo.api.web.activitymodule;

import com.swozo.persistence.activity.UserActivityModuleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserActivityModuleInfoRepository extends JpaRepository<UserActivityModuleInfo, Long> {
    @Query("FROM UserActivityModuleInfo uami " +
           "WHERE uami.user.id = :userId AND " +
            "uami.activityModuleScheduleInfo.scheduleRequestId = :scheduleRequestId AND " +
            "uami.activityModuleScheduleInfo.activityModule.id = :activityModuleId"
    )
    Optional<UserActivityModuleInfo> findUserActivityModuleInfoBy(Long activityModuleId, Long scheduleRequestId, Long userId);
}
