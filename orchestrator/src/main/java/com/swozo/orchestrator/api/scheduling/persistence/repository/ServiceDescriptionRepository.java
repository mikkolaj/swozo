package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface ServiceDescriptionRepository extends JpaRepository<ServiceDescriptionEntity, Long> {
    List<ServiceDescriptionEntity> findAllByStatusIn(Collection<ServiceStatus> status);
}
