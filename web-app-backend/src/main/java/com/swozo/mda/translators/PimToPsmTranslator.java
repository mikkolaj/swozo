package com.swozo.mda.translators;

import com.swozo.persistence.models.Pim;
import com.swozo.persistence.models.Psm;
import com.swozo.persistence.vmInfo.PimVmInfo;
import com.swozo.persistence.vmInfo.PsmVmInfo;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class PimToPsmTranslator{

    private String getMachineType(Integer vCPUs, Integer ram, Integer bandwith){
        //         TODO remember about repository of machines
        return "e2-medium";
    }

    private PsmVmInfo getPsmVmInfo(PimVmInfo pimVmInfo){
        PsmVmInfo psmVmInfo = new PsmVmInfo();
        psmVmInfo.setAmount(pimVmInfo.getAmount());
        psmVmInfo.setModuleIds(pimVmInfo.getModuleIds());
        psmVmInfo.setMachine_type(getMachineType(pimVmInfo.getVCPUs(), pimVmInfo.getRam(),
                pimVmInfo.getBandiwth()));
        psmVmInfo.setDisk(pimVmInfo.getDisk());

        return psmVmInfo;
    }

    public Psm getPsm(Pim pim) {
        PsmVmInfo psmTeacherVmInfo = getPsmVmInfo(pim.getTeacherVm());
        Optional<PsmVmInfo> psmStudentsVmsInfo = pim.getStudentsVms().map(this::getPsmVmInfo);

        return new Psm(psmTeacherVmInfo, psmStudentsVmsInfo);
    }
}
