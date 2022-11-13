package com.swozo.orchestrator.api.scheduling.persistence.repository;

import com.swozo.orchestrator.api.scheduling.persistence.entity.ServiceDescriptionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceDescriptionRepository extends JpaRepository<ServiceDescriptionEntity, Long> {
}
