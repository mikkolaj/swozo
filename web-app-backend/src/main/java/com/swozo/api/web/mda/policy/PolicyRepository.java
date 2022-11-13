package com.swozo.api.web.mda.policy;

import com.swozo.persistence.mda.policies.Policy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findAllByTeacherId(Long teacherId);
}
