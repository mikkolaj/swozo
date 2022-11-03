package com.swozo.api.web.mda.policy;

import com.swozo.persistence.mda.policies.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PolicyRepository extends JpaRepository<Policy, Long> {
}
