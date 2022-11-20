package com.swozo.persistence.mda.vminfo;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.servicemodule.ServiceModule;
import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PsmVmInfo {
    private Integer amount;
    private ArrayList<ServiceModule> serviceModules = new ArrayList<>();
    private String machineType;
    private Integer disk;

    @Override
    public String toString(){
        return String.format("PsmVmInfo(amount=%s, ServiceModuleIds=%s, machineType=%s, disk=%s)", amount, serviceModules.stream().map(BaseEntity::getId).toList(), machineType, disk);
    }
}
