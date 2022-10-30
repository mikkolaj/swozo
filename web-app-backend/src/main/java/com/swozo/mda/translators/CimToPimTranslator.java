package com.swozo.mda.translators;

import com.swozo.persistence.models.Cim;
import com.swozo.persistence.models.Pim;
import com.swozo.persistence.vminfo.PimVmInfo;
import com.swozo.util.mock.ServiceModule;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CimToPimTranslator{
    public Pim getPim(Cim cim) {
        Integer studentsVms = cim.getSelectedModules().stream().findFirst().map(x-> cim.getStudentsNumber()).orElse(0);
        Integer teacherVms = 1;
        PimVmInfo studentPimVmInfo = new PimVmInfo();
        studentPimVmInfo.setAmount(studentsVms);
        PimVmInfo teacherPimVmInfo = new PimVmInfo();
        teacherPimVmInfo.setAmount(teacherVms);

        for(ServiceModule module: cim.getSelectedModules()){
            if (module.isIsolated()){
                teacherPimVmInfo.concatenateRequirements(module.getPimVmInfo(studentsVms));
                studentPimVmInfo.concatenateRequirements(module.getPimVmInfo(studentsVms));
            }
            else {
                teacherPimVmInfo.concatenateRequirements(module.getPimVmInfo(studentsVms));
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
