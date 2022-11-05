package com.swozo.api.web.activitymodule;

import com.swozo.persistence.activity.ActivityModuleScheduleInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityScheduleInfoRepository extends JpaRepository<ActivityModuleScheduleInfo, Long> {
}
