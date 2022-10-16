package com.swozo.orchestrator.api.links.persistence.repository;

import com.swozo.orchestrator.api.links.persistence.entity.ActivityLinkInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityLinkInfoRepository extends JpaRepository<ActivityLinkInfoEntity, Long> {
    List<ActivityLinkInfoEntity> findAllByScheduleRequestId(@Param("scheduleRequestId") Long scheduleRequestId);
}
