package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public interface ScheduleRequestRepository extends JpaRepository<ScheduleRequestEntity, Long> {
    default List<ScheduleRequestEntity> findScheduleRequestsWithAllServiceDescriptionsInStatus(String status) {
        return findScheduleRequestsWithAllServiceDescriptionsInStatus(Set.of(status));
    }

    @Query(
            nativeQuery = true, value = """
            select sr.* from schedule_requests sr
            where not exists (
                select sd.schedule_request_id from service_descriptions sd
                where
                    sd.schedule_request_id = sr.id
                    and sd.status not in :statuses
            )"""
    )
    List<ScheduleRequestEntity> findScheduleRequestsWithAllServiceDescriptionsInStatus(@Param("statuses") Collection<String> statuses);

    List<ScheduleRequestEntity> findByServiceDescriptions_StatusIn(Collection<ServiceStatus> statuses);

    List<ScheduleRequestEntity> findByEndTimeGreaterThanAndServiceDescriptions_StatusIn(LocalDateTime dateTime, Collection<ServiceStatus> statuses);
}
