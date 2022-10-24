package com.swozo.persistence.vmInfo;

import com.swozo.util.mock.ModuleMock;
import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PIMVmInfo {
    private Integer amount = 0;
    private ArrayList<Long> moduleIds = new ArrayList<>();
    private Integer vCPUs = 0;
    private Integer ram = 0;
    private Integer disk = 0;
    private Integer bandiwth = 0;

    public void concatenateRequirements (PIMVmInfo other){
        for (Long moduleId : other.getModuleIds()){
            addModule(moduleId);
        }
        vCPUs += other.getVCPUs();
        ram += other.getRam();
        disk += other.getDisk();
        bandiwth += other.getBandiwth();
    }

    public void addModule(Long moduleId){
        if(moduleIds.contains(moduleId)){
            throw new IllegalArgumentException("module already added");
        }
        moduleIds.add(moduleId);
    }


}
