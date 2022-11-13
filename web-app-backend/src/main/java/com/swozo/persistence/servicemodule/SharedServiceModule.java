package com.swozo.persistence.servicemodule;

import com.swozo.api.web.servicemodule.dto.ServiceModuleMdaDto;
import com.swozo.persistence.mda.vminfo.PimVmInfo;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;

@Entity
@Getter
@Setter
@ToString
public class SharedServiceModule extends ServiceModule {
    private Integer usersPerAdditionalCore;
    private Integer usersPerAdditionalRamGb;
    private Integer usersPerAdditionalDiskGb;
    private Integer usersPerAdditionalBandwidthGbps;

    public PimVmInfo getPimVmInfo(Integer students) {
        PimVmInfo translation = new PimVmInfo();
        translation.addModule(this);
        translation.setVcpu(baseVcpu + students / usersPerAdditionalCore);
        translation.setRam(baseRam + students / usersPerAdditionalRamGb);
        translation.setDisk(baseDisk + students / usersPerAdditionalDiskGb);
        translation.setBandwidth(baseBandwidth + students / usersPerAdditionalBandwidthGbps);

        return translation;
    }

    @Override
    public Boolean isIsolated() {
        return false;
    }

    @Override
    public void setMdaData(ServiceModuleMdaDto mdaData) {
        super.setMdaData(mdaData);
        mdaData.sharedServiceModuleMdaDto().ifPresent(sharedMda -> {
           usersPerAdditionalCore = sharedMda.usersPerAdditionalCore();
           usersPerAdditionalRamGb = sharedMda.usersPerAdditionalRamGb();
           usersPerAdditionalDiskGb = sharedMda.usersPerAdditionalDiskGb();
           usersPerAdditionalBandwidthGbps = sharedMda.usersPerAdditionalBandwidthGbps();
        });
    }
}
