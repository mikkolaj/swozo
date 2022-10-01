package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.JupyterScheduleRequestEntity;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface JupyterScheduleRequestRepository extends ScheduleRequestBaseRepository<JupyterScheduleRequestEntity> {
}
