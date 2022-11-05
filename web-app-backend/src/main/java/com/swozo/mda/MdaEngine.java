package com.swozo.mda;

import com.swozo.mda.translators.CimToPimTranslator;
import com.swozo.mda.translators.PimToPsmTranslator;
import com.swozo.persistence.Course;
import com.swozo.persistence.activity.Activity;
import com.swozo.persistence.activity.ActivityModule;
import com.swozo.persistence.mda.models.Cim;
import com.swozo.persistence.mda.models.Pim;
import com.swozo.persistence.mda.models.Psm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MdaEngine {
    private final CimToPimTranslator cimToPimTranslator;
    private final PimToPsmTranslator pimToPsmTranslator;

    public Psm processCim(Course course, Activity activity) {
//    to remove and add cim as argument?
        Cim cim = buildCim(course, activity);

        Pim pim = cimToPimTranslator.getPim(cim);

//        what to do with PIM

        Psm psm = pimToPsmTranslator.getPsm(pim);


//        what to do with PSM

        return psm;
    }

    private Cim buildCim(Course course, Activity activity) {
        var cim = new Cim();
        cim.setSelectedModules(activity.getModules().stream()
                .map(ActivityModule::getServiceModule)
                .collect(Collectors.toCollection(ArrayList::new))
        );
        cim.setStudentsNumber(course.getExpectedStudentCount());
        cim.setTeacherId(course.getTeacher().getId());
        return cim;
    }
}
