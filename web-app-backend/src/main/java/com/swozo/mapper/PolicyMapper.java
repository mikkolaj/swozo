package com.swozo.mapper;

import com.swozo.api.web.mda.policy.PolicyRepository;
import com.swozo.api.web.mda.policy.dto.PolicyDto;
import com.swozo.api.web.mda.policy.request.CreatePolicyRequest;
import com.swozo.persistence.Course;
import com.swozo.persistence.mda.policies.Policy;
import com.swozo.persistence.mda.policies.PolicyType;
import com.swozo.persistence.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class PolicyMapper {
    @Autowired
    protected PolicyRepository policyRepository;

    @Autowired
    protected UserMapper userMapper;

//    @Autowired
//    protected PolicyType policyType;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "policyType", expression = "java(policyType)")
    @Mapping(target = "teacher", expression = "java(teacher)")
    @Mapping(target = "value", source = "createPolicyRequest.value")
    public abstract Policy toPersistence(CreatePolicyRequest createPolicyRequest, User teacher, PolicyType policyType);


    @Mapping(target = "id", source = "policy.id")
    @Mapping(target = "policyType", expression = "java(policyType)")
    @Mapping(target = "userDetailsDto", expression = "java(userMapper.toDto(policy.getTeacher()))")
    public  abstract PolicyDto toDto(Policy policy, String policyType);
}
