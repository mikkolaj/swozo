package com.swozo.mda.translators;

import com.swozo.persistence.models.Cim;
import com.swozo.persistence.models.Pim;
import com.swozo.persistence.vmInfo.PimVmInfo;
import com.swozo.util.mock.ModuleMock;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CimToPimTranslator{
    public Pim getPim(Cim cim) {
        Integer studentsVms = 0;
        if(cim.getSelectedModules().stream().anyMatch(ModuleMock::getIsolated))
            studentsVms = cim.getStudentsNumber();
        Integer teacherVms = 1;
        PimVmInfo studentPimVmInfo = new PimVmInfo();
        studentPimVmInfo.setAmount(studentsVms);
        PimVmInfo teacherPimVmInfo = new PimVmInfo();
        teacherPimVmInfo.setAmount(teacherVms);

        for(ModuleMock module: cim.getSelectedModules()){
            if (module.getIsolated()){
//                only if there is an isolated module we will return in PSM info for student vms
                studentsVms = cim.getStudentsNumber();
                teacherPimVmInfo.concatenateRequirements(module.getPimVmInfo(teacherVms));
                studentPimVmInfo.concatenateRequirements(module.getPimVmInfo(studentsVms));
            }
            else {
                teacherPimVmInfo.concatenateRequirements(module.getPimVmInfo(teacherVms + studentsVms));
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
