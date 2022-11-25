package com.swozo.api.web.activity;

import com.swozo.persistence.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("SELECT DISTINCT activity FROM Activity activity JOIN activity.course.students cs " +
           "WHERE (cs.id.userId = :userId OR cs.course.teacher.id = :userId) " +
           "AND activity.startTime >= :start AND activity.startTime <= :end")
    List<Activity> getAllUserActivitiesBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
