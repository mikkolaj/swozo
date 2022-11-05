package com.swozo.api.web.activitymodule;

import com.swozo.persistence.activity.ActivityModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ActivityModuleRepository extends JpaRepository<ActivityModule, Long> {
    int countActivityModulesByServiceModuleId(Long serviceModuleId);

    @Query(nativeQuery = true, value =
            "SELECT am.* " +
            "FROM activity_modules AS am " +
            "JOIN service_modules AS sm ON am.service_module_id = sm.id " +
            "JOIN activities AS a ON am.activity_id = a.id " +
            "WHERE sm.id = ?1 " +
            "ORDER BY a.created_at desc " +
            "offset ?2 limit ?3"
    )
    List<ActivityModule> getActivityModulesThatUseServiceModule(Long serviceModuleId, Long offset, Long limit);

    Optional<ActivityModule> findByServiceModuleId(Long serviceModuleId);
}
