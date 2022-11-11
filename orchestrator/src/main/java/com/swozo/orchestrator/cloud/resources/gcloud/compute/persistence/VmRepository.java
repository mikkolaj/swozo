package com.swozo.orchestrator.cloud.resources.gcloud.compute.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VmRepository extends JpaRepository<VmEntity, Long> {
    @Query("select vm from VmEntity vm WHERE vm.status = 0 and vm.id not in (select request.vmResourceId from ScheduleRequestEntity request)")
    List<VmEntity> findAllCreatedWithBrokenMetadata();
}
