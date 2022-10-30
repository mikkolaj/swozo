package com.swozo.util.mock;

import com.swozo.persistence.models.Cim;
import lombok.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString
public class CIMMock {
    public Cim generateMockCIM(){
        IsolatedServiceModule moduleMock1 = new IsolatedServiceModule(1L,4,64,14,8);
        SharedServiceModule moduleMock2 = new SharedServiceModule(2L,4,64,28,8,3,3,1,1);
        ArrayList<ServiceModule> selectedModules = new ArrayList<>(List.of(moduleMock1, moduleMock2));
        return new Cim(selectedModules, 1L, 20);
    }
}
