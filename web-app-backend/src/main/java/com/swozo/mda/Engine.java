package com.swozo.mda;

import com.swozo.mda.translators.CimToPimTranslator;
import com.swozo.mda.translators.PimToPsmTranslator;
import com.swozo.persistence.mda.models.Cim;
import com.swozo.persistence.mda.models.Pim;
import com.swozo.persistence.mda.models.Psm;
import com.swozo.util.mock.CIMMock;
import lombok.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Engine {
    private final CimToPimTranslator cimToPimTranslator;
    private final PimToPsmTranslator pimToPsmTranslator;

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
