package com.swozo.webservice.repository;

import com.swozo.databasemodel.Activity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
}
