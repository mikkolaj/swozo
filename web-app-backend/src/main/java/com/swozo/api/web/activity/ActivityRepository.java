package com.swozo.api.web.activity;

import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.activity.ActivityModuleScheduleInfo;
import com.swozo.persistence.activity.UserActivityLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {

    default List<UserActivityLink> getLinksForUser(Long activityId, Long userId) {
        // TODO for demonstration that its possible
        var activity = this.getById(activityId);
        return activity.getModules().stream()
                .map(ActivityModule::getSchedules)
                .flatMap(Collection::stream)
                .map(ActivityModuleScheduleInfo::getUserActivityLinks)
                .flatMap(Collection::stream)
                .filter(userActivityLink -> userActivityLink.getUser().getId().equals(userId))
                .toList();
    }
}
