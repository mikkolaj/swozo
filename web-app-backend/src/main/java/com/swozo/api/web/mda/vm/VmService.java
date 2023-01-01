package com.swozo.api.web.mda.vm;

import com.swozo.api.web.mda.vm.dto.VmDto;
import com.swozo.api.web.mda.vm.request.CreateVmRequest;
import com.swozo.api.web.mda.vm.request.EditVmRequest;
import com.swozo.mapper.VmMapper;
import com.swozo.persistence.mda.VirtualMachine;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class VmService {
    private static final int MIN_IMAGE_SIZE_GB = 10;
    private final VmRepository vmRepository;
    private final VmMapper vmMapper;

    public Collection<VmDto> getAllSystemVmsDto(){
        return getAllSystemVms().stream().map(vmMapper::toDto).toList();
    }

    public Collection<VirtualMachine> getAllSystemVms(){
        return vmRepository.findAll();
    }

    public VmDto createVm(CreateVmRequest createVmRequest){
        VirtualMachine vm = vmMapper.toPersistence(createVmRequest);
        if (createVmRequest.imageDiskSizeGB() == null || createVmRequest.imageDiskSizeGB() < MIN_IMAGE_SIZE_GB) {
            vm.setImageDiskSizeGB(MIN_IMAGE_SIZE_GB);
        }

        vmRepository.save(vm);
        return vmMapper.toDto(vm);
    }

    public void deleteVm(Long id){
        vmRepository.deleteById(id);
    }

    public VmDto editVm(Long id, EditVmRequest request){
        VirtualMachine vm = vmRepository.getById(id);

        request.name().ifPresent(vm::setName);
        request.vCpu().ifPresent(vm::setVcpu);
        request.ramGB().ifPresent(vm::setRamGB);
        request.imageDiskSizeGB().ifPresent(vm::setImageDiskSizeGB);
        request.bandwidthMbps().ifPresent(vm::setBandwidthMbps);

        vmRepository.save(vm);

        return vmMapper.toDto(vm);
    }
}
