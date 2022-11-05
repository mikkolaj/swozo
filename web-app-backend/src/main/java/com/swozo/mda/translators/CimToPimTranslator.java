package com.swozo.mda.translators;

import com.swozo.api.web.mda.policy.PolicyService;
import com.swozo.persistence.mda.models.Cim;
import com.swozo.persistence.mda.models.Pim;
import com.swozo.persistence.mda.policies.Policy;
import com.swozo.persistence.mda.vminfo.PimVmInfo;
import com.swozo.persistence.servicemodule.ServiceModule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CimToPimTranslator{
    private final PolicyService policyService;

    private void checkPolicies(Collection<Policy> policies, PimVmInfo pimVmInfo){
        for (Policy policy: policies){
            policy.checkPolicy(pimVmInfo);
        }
    }

    public Pim getPim(Cim cim) {
        Integer studentsVms = cim.getSelectedModules().stream().filter(ServiceModule::isIsolated)
                .findFirst().map(x-> cim.getStudentsNumber()).orElse(0);
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

        Collection<Policy> policiesToCheck = policyService.getAllTeacherPolicies(cim.getTeacherId());


//        THROWABLE:
        checkPolicies(policiesToCheck, teacherPimVmInfo);
        checkPolicies(policiesToCheck, studentPimVmInfo);



//        TODO check policies
        return pim;
    }
}
