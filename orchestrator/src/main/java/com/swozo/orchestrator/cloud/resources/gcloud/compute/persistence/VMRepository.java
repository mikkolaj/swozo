package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VMRepository extends JpaRepository<VMEntity, Long> {
}
