package com.swozo.persistence.mda.models;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.servicemodule.ServiceModule;
import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Cim{
    private ArrayList<ServiceModule> selectedModules = new ArrayList<>();
    private Long teacherId;
    private Integer studentsNumber;

    @Override
    public String toString(){
        return String.format("Cim(ServiceModuleIds:%s, teacherId:%s, studentsNumber:%s)", selectedModules.stream().map(BaseEntity::getId).toList(), teacherId, studentsNumber);
    }
}
