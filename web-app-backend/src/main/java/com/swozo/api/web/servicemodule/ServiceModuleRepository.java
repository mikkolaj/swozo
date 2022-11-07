package com.swozo.api.web.servicemodule;

import com.swozo.persistence.servicemodule.ServiceModule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ServiceModuleRepository extends JpaRepository<ServiceModule, Long> {
    @Query("from ServiceModule serviceModule where serviceModule.creator.id = :creatorId")
    List<ServiceModule> getAllModulesCreatedBy(Long creatorId);

    List<ServiceModule> getAllByIsPublicTrueAndReadyIsTrue();

    Optional<ServiceModule> findByName(String name);
}
