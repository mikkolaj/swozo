package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ScheduleRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface ScheduleRequestBaseRepository<T extends ScheduleRequestEntity> extends JpaRepository<T, Long> {
}
