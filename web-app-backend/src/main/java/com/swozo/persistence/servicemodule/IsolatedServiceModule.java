package com.swozo.persistence.servicemodule;

import com.swozo.persistence.mda.vminfo.PimVmInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
public class IsolatedServiceModule extends ServiceModule {
    public PimVmInfo getPimVmInfo(Integer students){
        PimVmInfo translation = new PimVmInfo();
        translation.addModule(this);
        translation.setVcpu(baseVcpu);
        translation.setRam(baseRamGB);
        translation.setDisk(baseDiskGB);
        translation.setBandwidth(baseBandwidthMbps);

        return translation;
    }

    @Override
    public Boolean isIsolated() {
        return true;
    }
}
