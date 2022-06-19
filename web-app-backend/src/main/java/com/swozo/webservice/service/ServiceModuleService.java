package com.swozo.webservice.service;

import com.swozo.databasemodel.ServiceModule;
import com.swozo.dto.servicemodule.ServiceModuleDetailsResp;
import com.swozo.mapper.ServiceModuleMapper;
import com.swozo.webservice.exceptions.ServiceModuleNotFoundException;
import com.swozo.webservice.repository.ServiceModuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.LinkedList;

@Service
@RequiredArgsConstructor
public class ServiceModuleService {
    ServiceModuleRepository serviceModuleRepository;
    ServiceModuleMapper serviceModuleMapper;

    ServiceModule getServiceModule(Long id){
        return serviceModuleRepository.findById(id)
                .orElseThrow(() -> new ServiceModuleNotFoundException(id));
    }

    public Collection<ServiceModuleDetailsResp> getServiceModuleList(){
//        TODO To ma byc niby lista wszystkich dsotępnych modułów (czyli nei activity modułów tylko serwis modułów...
        return new LinkedList<>();
    }

    public ServiceModuleDetailsResp getServiceModuleInfo(Long serviceModuleId){
        ServiceModule serviceModule = getServiceModule(serviceModuleId);
        return serviceModuleMapper.toModel(serviceModule);
    }
}
