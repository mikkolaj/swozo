package com.swozo.api.web.servicemodule;

import com.swozo.api.web.servicemodule.dto.ServiceModuleDetailsDto;
import com.swozo.mapper.ServiceModuleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ServiceModuleService {
    private final ServiceModuleRepository serviceModuleRepository;
    private final ServiceModuleMapper serviceModuleMapper;

    public Collection<ServiceModuleDetailsDto> getServiceModuleList() {
        return serviceModuleRepository.findAll().stream().map(serviceModuleMapper::toDto).toList();
    }

    public ServiceModuleDetailsDto getServiceModuleInfo(Long serviceModuleId) {
        var serviceModule = serviceModuleRepository.getById(serviceModuleId);
        return serviceModuleMapper.toDto(serviceModule);
    }
}
