package com.swozo.api.web.servicemodule;

import com.swozo.persistence.ServiceModule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceModuleRepository extends JpaRepository<ServiceModule, Long> {
}
