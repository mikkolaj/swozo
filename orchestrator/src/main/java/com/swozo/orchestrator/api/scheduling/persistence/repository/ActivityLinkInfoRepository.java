package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ActivityLinkInfoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ActivityLinkInfoRepository extends JpaRepository<ActivityLinkInfoEntity, Long> {
    @Query("SELECT a FROM ActivityLinkInfoEntity a where a.scheduleRequest = :scheduleRequestId")
    List<ActivityLinkInfoEntity> findAllByScheduleRequestId(@Param("scheduleRequestId") Long scheduleRequestId);
}
