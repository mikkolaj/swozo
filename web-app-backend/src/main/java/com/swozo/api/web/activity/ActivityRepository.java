package com.swozo.api.web.activity;

import com.swozo.persistence.activity.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    @Query("SELECT DISTINCT activity FROM Activity activity LEFT JOIN activity.course.students cs " +
           "WHERE (cs.id.userId = :userId OR activity.course.teacher.id = :userId) " +
           "AND activity.startTime >= :start AND activity.startTime <= :end")
    List<Activity> getAllUserActivitiesBetween(Long userId, LocalDateTime start, LocalDateTime end);

    @Query("SELECT activity FROM Activity activity " +
           "WHERE activity.cancelled = false AND activity.startTime >= :start AND activity.startTime <= :end")
    List<Activity> getAllNotCancelledActivitiesBetween(LocalDateTime start, LocalDateTime end);
}
