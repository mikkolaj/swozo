package com.swozo.webservice.service;

import com.swozo.databasemodel.ServiceModule;
import com.swozo.dto.servicemodule.ServiceModuleDetailsDto;
import com.swozo.mapper.ServiceModuleMapper;
import com.swozo.webservice.exceptions.ServiceModuleNotFoundException;
import com.swozo.webservice.repository.ServiceModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ServiceModuleService {
    private final ServiceModuleRepository serviceModuleRepository;
    private final ServiceModuleMapper serviceModuleMapper;

    public ServiceModule getServiceModule(Long id) {
        return serviceModuleRepository.findById(id)
                .orElseThrow(() -> new ServiceModuleNotFoundException(id));
    }

    public Collection<ServiceModuleDetailsDto> getServiceModuleList() {
//        TODO To ma byc niby lista wszystkich dsotępnych modułów (czyli nei activity modułów tylko serwis modułów...
        return serviceModuleRepository.findAll().stream().map(serviceModuleMapper::toDto).toList();
    }

    public ServiceModuleDetailsDto getServiceModuleInfo(Long serviceModuleId) {
        ServiceModule serviceModule = getServiceModule(serviceModuleId);
        return serviceModuleMapper.toDto(serviceModule);
    }
}
