package com.swozo.mda.translators;

import com.swozo.persistence.models.Cim;
import com.swozo.persistence.models.Pim;
import com.swozo.persistence.vmInfo.PIMVmInfo;
import com.swozo.util.mock.ModuleMock;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CimToPimTranslator{

    private PIMVmInfo translateIsolatedModule(ModuleMock module){
        PIMVmInfo translation = new PIMVmInfo();

        translation.addModule(module.getId());
        translation.setVCPUs(module.getBaseVcpu());
        translation.setRam(module.getBaseRam());
        translation.setDisk(module.getBaseDisk());
        translation.setBandiwth(module.getBaseBanwidth());

        return translation;
    }


    private PIMVmInfo translateNotIsolatedModule(ModuleMock module, Integer students){
        PIMVmInfo translation = new PIMVmInfo();

        translation.addModule(module.getId());
        translation.setVCPUs(module.getBaseVcpu() + students / module.getUsersPerAdditionalCore());
        translation.setRam(module.getBaseRam() + students / module.getUsersPerAdditionalRamGb());
        translation.setDisk(module.getBaseDisk() + students / module.getUsersPerAdditionalDiskGb());
        translation.setBandiwth(module.getBaseBanwidth() + students / module.getUsersPerAdditionalBandiwthGbps());

        return translation;
    }

    public Pim getPim(Cim cim) {
        Integer studentsNumber = cim.getStudentsNumber();
        PIMVmInfo studentPimVmInfo = new PIMVmInfo();
        Integer studentsVms = 0;
        PIMVmInfo teacherPimVmInfo = new PIMVmInfo();
        teacherPimVmInfo.setAmount(1);

        for(ModuleMock module: cim.getSelectedModules()){
            if (module.getIsolated()){
//                only if there is an isolated module we will return in PSM info for student vms
                studentsVms = cim.getStudentsNumber();
                teacherPimVmInfo.concatenateRequirements(translateIsolatedModule(module));
                studentPimVmInfo.concatenateRequirements(translateIsolatedModule(module));
            }
            else {
                teacherPimVmInfo.concatenateRequirements(translateNotIsolatedModule(module, studentsNumber));
            }
        }
        Pim pim = new Pim();
        pim.setTeacherVm(teacherPimVmInfo);
        if(studentsVms != 0) {
            studentPimVmInfo.setAmount(studentsVms);
            pim.setStudentsVms(Optional.of(studentPimVmInfo));
        }
//        TODO check policies
        return pim;
    }
}
