package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VMRepository extends JpaRepository<VMEntity, Long> {
    @Query("select vm from VMEntity vm WHERE vm.status = 0 and vm.id not in (select request.vmResourceId from ScheduleRequestEntity request)")
    List<VMEntity> findAllCreatedWithBrokenMetadata();
}
