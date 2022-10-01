package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScheduleRequestRepository extends JpaRepository<ScheduleRequestEntity, Long> {
}
