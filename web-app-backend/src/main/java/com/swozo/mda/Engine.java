package com.swozo.mda;

import com.swozo.mda.translators.CimToPimTranslator;
import com.swozo.mda.translators.PimToPsmTranslator;
import com.swozo.persistence.models.Cim;
import com.swozo.persistence.models.Pim;
import com.swozo.persistence.models.Psm;
import com.swozo.util.mock.CIMMock;
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

        return psm;
    }
}
