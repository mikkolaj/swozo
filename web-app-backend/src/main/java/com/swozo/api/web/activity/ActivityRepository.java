package com.swozo.api.web.activity;

import com.swozo.persistence.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
