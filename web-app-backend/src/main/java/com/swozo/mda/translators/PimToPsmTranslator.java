package com.swozo.mda.translators;

import com.swozo.api.web.exceptions.types.mda.NeededVmNotFound;
import com.swozo.api.web.mda.vm.VmService;
import com.swozo.persistence.mda.VirtualMachine;
import com.swozo.persistence.mda.models.Pim;
import com.swozo.persistence.mda.models.Psm;
import com.swozo.persistence.mda.vminfo.PimVmInfo;
import com.swozo.persistence.mda.vminfo.PsmVmInfo;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PimToPsmTranslator{
    private final VmService vmService;

    private String getMachineType(Integer vcpu, Integer ram, Integer bandwidth){
        Collection<VirtualMachine> vms = vmService.getAllSystemVms();
        Collection<VirtualMachine> possibleVms = vms.stream().filter(vm ->
                vm.getVcpu() >= vcpu && vm.getRam() >= ram && vm.getBandwidth() >= bandwidth).toList();

        Integer minVcpu = Collections.min(possibleVms.stream().map(VirtualMachine::getVcpu).toList());
        Collection<VirtualMachine> minVcpuVms = possibleVms.stream().filter(vm -> vm.getVcpu() == minVcpu).toList();

        Integer minRam = Collections.min(possibleVms.stream().map(VirtualMachine::getRam).toList());
        Collection<VirtualMachine> minRamVms = minVcpuVms.stream().filter(vm -> vm.getRam() == minRam).toList();

        Integer minBandwidth = Collections.min(possibleVms.stream().map(VirtualMachine::getBandwidth).toList());
        Collection<VirtualMachine> minBandwidthVms = minRamVms.stream().filter(vm -> vm.getBandwidth() == minBandwidth).toList();

        Optional<VirtualMachine> selectedVm = minBandwidthVms.stream().findFirst();

        if(selectedVm.isPresent()){
            return selectedVm.get().getName();
        }
        else
            throw NeededVmNotFound.withConditions(vcpu, ram, bandwidth);
    }

    private PsmVmInfo getPsmVmInfo(PimVmInfo pimVmInfo){
        PsmVmInfo psmVmInfo = new PsmVmInfo();
        psmVmInfo.setAmount(pimVmInfo.getAmount());
        psmVmInfo.setModuleIds(pimVmInfo.getModuleIds());
        psmVmInfo.setMachine_type(getMachineType(pimVmInfo.getVcpu(), pimVmInfo.getRam(),
                pimVmInfo.getBandwidth()));
        psmVmInfo.setDisk(pimVmInfo.getDisk());

        return psmVmInfo;
    }

    public Psm getPsm(Pim pim) {
        PsmVmInfo psmTeacherVmInfo = getPsmVmInfo(pim.getTeacherVm());
        Optional<PsmVmInfo> psmStudentsVmsInfo = pim.getStudentsVms().map(this::getPsmVmInfo);

        return new Psm(psmTeacherVmInfo, psmStudentsVmsInfo);
    }
}
