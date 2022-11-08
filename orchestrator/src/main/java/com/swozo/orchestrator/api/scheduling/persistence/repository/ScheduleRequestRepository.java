package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface ScheduleRequestRepository extends JpaRepository<ScheduleRequestEntity, Long> {
    List<ScheduleRequestEntity> findByEndTimeLessThanAndServiceDescriptions_StatusIn(LocalDateTime dateTime, Collection<ServiceStatus> status);

    List<ScheduleRequestEntity> findByServiceDescriptions_StatusEquals(ServiceStatus status);

    List<ScheduleRequestEntity> findByEndTimeGreaterThanAndServiceDescriptions_StatusIn(LocalDateTime dateTime, Collection<ServiceStatus> status);

    List<ScheduleRequestEntity> findByServiceDescriptions_AllStatusIn(Collection<ServiceStatus> status);
}
