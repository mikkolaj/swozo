package com.swozo.mapper;

import com.swozo.api.web.mda.vm.VmRepository;
import com.swozo.api.web.mda.vm.dto.VmDto;
import com.swozo.api.web.mda.vm.request.CreateVmRequest;
import com.swozo.persistence.mda.VirtualMachine;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(componentModel = "spring")
public abstract class VmMapper {
    @Autowired
    protected VmRepository vmRepository;

    @Mapping(target = "id", ignore = true)
    public abstract VirtualMachine toPersistence(CreateVmRequest createVmRequest);


    @Mapping(target = "id", source = "virtualMachine.id")
    public  abstract VmDto toDto(VirtualMachine virtualMachine);
}
