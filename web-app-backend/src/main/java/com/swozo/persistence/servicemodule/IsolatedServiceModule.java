package com.swozo.persistence.servicemodule;

import com.swozo.persistence.mda.vminfo.PimVmInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "IsolatedServiceModules")
@Getter
@Setter
@ToString
public class IsolatedServiceModule extends ServiceModule {
    public PimVmInfo getPimVmInfo(Integer studens){
        PimVmInfo translation = new PimVmInfo();
        translation.addModule(this);
        translation.setVcpu(baseVcpu);
        translation.setRam(baseRam);
        translation.setDisk(baseDisk);
        translation.setBandwidth(baseBandwidth);

        return translation;
    }

    @Override
    public Boolean isIsolated() {
        return true;
    }
}
