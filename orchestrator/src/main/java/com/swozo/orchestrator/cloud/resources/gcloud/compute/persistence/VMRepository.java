package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import com.swozo.orchestrator.cloud.resources.gcloud.compute.model.VMStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface VMRepository extends JpaRepository<VMEntity, Long> {
    Stream<VMEntity> findAllByStatusEquals(VMStatus status);
}
