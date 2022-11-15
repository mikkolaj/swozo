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
public class PimVmInfo {
    private Integer amount = 0;
    private ArrayList<ServiceModule> serviceModules = new ArrayList<>();
    private Integer vcpu = 0;
    private Integer ram = 0;
    private Integer disk = 0;
    private Integer bandwidth = 0;

    public void concatenateRequirements (PimVmInfo other){
        for (var module: other.getServiceModules()){
            addModule(module);
        }
        vcpu += other.getVcpu();
        ram += other.getRam();
        disk += other.getDisk();
        bandwidth += other.getBandwidth();
    }

    public void addModule(ServiceModule serviceModule){
        if(serviceModules.contains(serviceModule)){
            throw new IllegalArgumentException("module already added");
        }
        serviceModules.add(serviceModule);
    }

    @Override
    public String toString(){
        return String.format("PimVmInfo(amount:%s, ServiceModuleIds:%s, vcpu:%s, ramGB:%s, imageDiskSizeGB:%s, bandwidthMbps:%s)", amount, serviceModules.stream().map(BaseEntity::getId).toList(), vcpu, ram, disk, bandwidth);
    }
}
