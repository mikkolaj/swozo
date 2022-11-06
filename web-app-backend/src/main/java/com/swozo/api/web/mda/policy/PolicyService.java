package com.swozo.api.web.mda.policy;

import com.swozo.api.web.mda.policy.dto.PolicyDto;
import com.swozo.api.web.mda.policy.request.CreatePolicyRequest;
import com.swozo.api.web.mda.policy.request.EditPolicyRequest;
import com.swozo.api.web.user.UserService;
import com.swozo.mapper.PolicyMapper;
import com.swozo.persistence.mda.policies.Policy;
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

    public Collection<PolicyDto> getAllSystemPoliciesDto(){
        return policyRepository.findAll().stream().map(policyMapper::toDto).toList();
    }

    public Collection<PolicyDto> getAllTeacherPoliciesDto(Long teacherId){
        return getAllTeacherPolicies(teacherId).stream().map(policyMapper::toDto).toList();
    }

    public Collection<Policy> getAllTeacherPolicies(Long teacherId){
        return policyRepository.findAllByTeacherId(teacherId);
    }

    public PolicyDto createPolicy(CreatePolicyRequest createPolicyRequest){
        User teacher = userService.getUserById(createPolicyRequest.teacherId());
        Policy policy = policyMapper.toPersistence(createPolicyRequest, teacher);
        policyRepository.save(policy);

        return policyMapper.toDto(policy);
    }

    public void deletePolicy(Long id){
        policyRepository.deleteById(id);
    }

    public PolicyDto editPolicy(Long id, EditPolicyRequest request){
        Policy policy = policyRepository.getById(id);

        request.policyType().ifPresent(policy::setPolicyType);
        request.teacherId().ifPresent(teacherId -> policy.setTeacher(userService.getUserById(teacherId)));
        request.value().ifPresent(policy::setValue);

        policyRepository.save(policy);

        return policyMapper.toDto(policy);
    }
}
