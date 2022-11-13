package com.swozo.mda.translators;

import com.swozo.api.web.exceptions.types.mda.NeededVmNotFound;
import com.swozo.api.web.mda.vm.VmService;
import com.swozo.persistence.mda.VirtualMachine;
import com.swozo.persistence.mda.models.Pim;
import com.swozo.persistence.mda.models.Psm;
import com.swozo.persistence.mda.vminfo.PimVmInfo;
import com.swozo.persistence.mda.vminfo.PsmVmInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PimToPsmTranslator{
    private final VmService vmService;

    private VirtualMachine selectVirtualMachine(Integer vcpu, Integer ram, Integer bandwidth){
        Collection<VirtualMachine> vms = vmService.getAllSystemVms();
        Collection<VirtualMachine> possibleVms = vms.stream().filter(vm ->
                vm.getVcpu() >= vcpu && vm.getRam() >= ram && vm.getBandwidth() >= bandwidth).toList();

        if (possibleVms.isEmpty()) {
            throw NeededVmNotFound.withConditions(vcpu, ram, bandwidth);
        }

        return Collections.min(possibleVms,
                Comparator.comparingInt(VirtualMachine::getVcpu)
                        .thenComparing(VirtualMachine::getRam)
                        .thenComparing(VirtualMachine::getBandwidth)
        );
    }

    private PsmVmInfo getPsmVmInfo(PimVmInfo pimVmInfo){
        PsmVmInfo psmVmInfo = new PsmVmInfo();
        VirtualMachine virtualMachine = selectVirtualMachine(pimVmInfo.getVcpu(), pimVmInfo.getRam(),
                pimVmInfo.getBandwidth());

        psmVmInfo.setAmount(pimVmInfo.getAmount());
        psmVmInfo.setServiceModules(pimVmInfo.getServiceModules());
        psmVmInfo.setMachineType(virtualMachine.getName());
        psmVmInfo.setDisk(virtualMachine.getImageDiskSize() + pimVmInfo.getDisk());

        return psmVmInfo;
    }

    public Psm getPsm(Pim pim) {
        PsmVmInfo psmTeacherVmInfo = getPsmVmInfo(pim.getTeacherVm());
        Optional<PsmVmInfo> psmStudentsVmsInfo = pim.getStudentsVms().map(this::getPsmVmInfo);

        return new Psm(psmTeacherVmInfo, psmStudentsVmsInfo);
    }
}
