package com.swozo.api.web.mda.policy;

import com.swozo.api.web.mda.policy.dto.PolicyDto;
import com.swozo.api.web.mda.policy.request.CreatePolicyRequest;
import com.swozo.api.web.mda.policy.request.EditPolicyRequest;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.PolicyMapper;
import com.swozo.persistence.mda.policies.Policy;
import com.swozo.persistence.mda.policies.PolicyType;
import com.swozo.persistence.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class PolicyService {
    private final PolicyRepository policyRepository;
    private final UserService userService;
    private final PolicyMapper policyMapper;

    public Collection<PolicyDto> getAllSystemPolicies(){
        return policyRepository.findAll().stream().map(policy ->
                policyMapper.toDto(policy, policy.getPolicyType().name())).toList();
    }

    public Collection<PolicyDto> getAllTeacherPoliciesDto(Long teacherId){
        return policyRepository.findAll().stream().filter(policy -> Objects.equals(policy.getTeacher().getId(), teacherId)).map(policy ->
                policyMapper.toDto(policy, policy.getPolicyType().name())).toList();
    }

    public Collection<Policy> getAllTeacherPolicies(Long teacherId){
        return policyRepository.findAll().stream().
                filter(policy -> Objects.equals(policy.getTeacher().getId(), teacherId)).toList();
    }

    public PolicyDto createPolicy(CreatePolicyRequest createPolicyRequest){
        User teacher = userService.getUserById(createPolicyRequest.teacherId());
        PolicyType policyType = PolicyType.valueOf(createPolicyRequest.policyType());
        Policy policy = policyMapper.toPersistence(createPolicyRequest, teacher, policyType);
        policyRepository.save(policy);

        return policyMapper.toDto(policy, policyType.name());
    }

    public void deletePolicy(Long id){
        policyRepository.deleteById(id);
    }

    public PolicyDto editPolicy(Long id, EditPolicyRequest request){
        Policy policy = policyRepository.getById(id);

        request.policyType().ifPresent(policyType -> policy.setPolicyType(PolicyType.valueOf(policyType)));
        request.teacherId().ifPresent(teacherId -> policy.setTeacher(userService.getUserById(teacherId)));
        request.value().ifPresent(policy::setValue);

        policyRepository.save(policy);

        return policyMapper.toDto(policy, policy.getPolicyType().name());
    }
}
