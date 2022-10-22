package com.swozo.api.web.activitymodule;

import com.swozo.persistence.activity.ActivityModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityModuleRepository extends JpaRepository<ActivityModule, Long> {
    int countActivityModulesByServiceModuleId(Long serviceModuleId);
}
