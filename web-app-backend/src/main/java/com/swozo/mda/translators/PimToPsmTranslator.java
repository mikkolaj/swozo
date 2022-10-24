package com.swozo.mda.translators;

import com.swozo.persistence.models.Pim;
import com.swozo.persistence.models.Psm;
import com.swozo.persistence.vmInfo.PIMVmInfo;
import com.swozo.persistence.vmInfo.PSMVmInfo;
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

    private PSMVmInfo getPsmVmInfo(PIMVmInfo pimVmInfo){
        PSMVmInfo psmVmInfo = new PSMVmInfo();
        psmVmInfo.setAmount(pimVmInfo.getAmount());
        psmVmInfo.setModuleIds(pimVmInfo.getModuleIds());
        psmVmInfo.setMachine_type(getMachineType(pimVmInfo.getVCPUs(), pimVmInfo.getRam(),
                pimVmInfo.getBandiwth()));
        psmVmInfo.setDisk(pimVmInfo.getDisk());

        return psmVmInfo;
    }

    public Psm getPsm(Pim pim) {
        PSMVmInfo psmTeacherVmInfo = getPsmVmInfo(pim.getTeacherVm());
        Optional<PSMVmInfo> psmStudentsVmsInfo = Optional.empty();

        if(pim.getStudentsVms().isPresent()){
            psmStudentsVmsInfo = Optional.of(getPsmVmInfo(pim.getStudentsVms().get()));
        }

        return new Psm(psmTeacherVmInfo, psmStudentsVmsInfo);
    }
}
