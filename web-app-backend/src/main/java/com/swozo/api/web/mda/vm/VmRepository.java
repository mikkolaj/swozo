package com.swozo.api.web.mda.vm;

import com.swozo.persistence.mda.VirtualMachine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VmRepository extends JpaRepository<VirtualMachine, Long> {
}
