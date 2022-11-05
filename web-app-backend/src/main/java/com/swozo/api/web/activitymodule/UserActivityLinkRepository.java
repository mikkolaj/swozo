package com.swozo.api.web.activitymodule;

import com.swozo.persistence.activity.UserActivityLink;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserActivityLinkRepository extends JpaRepository<UserActivityLink, Long> {

}
