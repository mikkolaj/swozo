package com.swozo.util.mock;

import com.swozo.persistence.mda.models.Cim;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CIMMock {
    public Cim generateMockCIM(){
        IsolatedServiceModule moduleMock1 = new IsolatedServiceModule(1L,1,1,14,3);
        SharedServiceModule moduleMock2 = new SharedServiceModule(2L,1,1,28,3,30,10,1,10);
        ArrayList<ServiceModule> selectedModules = new ArrayList<>(List.of(moduleMock1, moduleMock2));
        return new Cim(selectedModules, 2L, 20);
    }
}
