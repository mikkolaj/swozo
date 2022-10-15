package com.swozo.mda.engine;

import com.swozo.mda.engine.translators.CimToPimTranslator;
import com.swozo.mda.engine.translators.PimToPsmTranslator;
import com.swozo.mda.persistance.models.Cim;
import com.swozo.mda.persistance.models.Pim;
import com.swozo.mda.persistance.models.Psm;
import com.swozo.mda.util.mock.CIMMock;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Engine {
    private CimToPimTranslator cimToPimTranslator = new CimToPimTranslator();
    private PimToPsmTranslator pimToPsmTranslator = new PimToPsmTranslator();

    public Psm processCim() {
//    to remove and add cim as argument?
        CIMMock cimMock = new CIMMock();
        Cim cim = cimMock.generateMockCIM();

        Pim pim = cimToPimTranslator.getPim(cim);

//        what to do with PIM

        Psm psm = pimToPsmTranslator.getPsm(pim);


//        what to do with PSM

        return (Psm) psm;
    }
}
