package com.swozo.api.web.user;

import com.swozo.persistence.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User getByEmail(String email);

    @Query(nativeQuery = true, value =
            "SELECT u.* " +
                    "FROM user_activity_links ual " +
                    "JOIN activity_schedule_requests asr ON ual.activity_module_schedule_info_id = asr.id " +
                    "JOIN users u ON ual.user_id = u.id " +
                    "WHERE asr.activity_module_id = ?1 AND asr.schedule_request_id = ?2"
    )
    List<User> getUsersThatUseVmCreatedIn(Long activityModuleId, Long scheduleRequestId);
}
