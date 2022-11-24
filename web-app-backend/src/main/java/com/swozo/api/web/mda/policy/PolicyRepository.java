package com.swozo.api.web.mda.policy;

import com.swozo.persistence.mda.policies.Policy;
import com.swozo.persistence.mda.policies.PolicyType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface PolicyRepository extends JpaRepository<Policy, Long> {

    List<Policy> findAllByTeacherId(Long teacherId);

    void deleteAllByTeacherId(Long teacherId);

    default List<Policy> getAllNotMdaDependantByTeacherId(Long teacherId) {
        return findAllByTeacherId(teacherId).stream()
                .filter(policy -> !policy.getPolicyType().isMdaDependant())
                .toList();
    }

    default Map<PolicyType, Policy> getAllNotMdaDependantByTeacherIdMap(Long teacherId) {
        return getAllNotMdaDependantByTeacherId(teacherId).stream()
                .collect(Collectors.toMap(
                        Policy::getPolicyType,
                        Function.identity())
                );
    }
}
