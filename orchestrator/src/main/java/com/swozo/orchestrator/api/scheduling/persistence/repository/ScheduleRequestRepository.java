package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.RequestStatus;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRequestRepository extends JpaRepository<ScheduleRequestEntity, Long> {
    List<ScheduleRequestEntity> findByEndTimeLessThanAndStatusNot(LocalDateTime dateTime, RequestStatus status);
}
