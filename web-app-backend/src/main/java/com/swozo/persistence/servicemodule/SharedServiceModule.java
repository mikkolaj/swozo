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
@ToString(callSuper = true)
public class SharedServiceModule extends ServiceModule {
    private final static int MB_MULTIPLIER = 1024;
    private Integer usersPerAdditionalCore;
    private Integer usersPerAdditionalRamGb;
    private Integer usersPerAdditionalDiskGb;
    private Integer usersPerAdditionalBandwidthGbps;

    public PimVmInfo getPimVmInfo(Integer students) {
        PimVmInfo translation = new PimVmInfo();
        translation.addModule(this);
        translation.setVcpu(baseVcpu + students / usersPerAdditionalCore);
        translation.setRam(baseRamGB + students / usersPerAdditionalRamGb);
        translation.setDisk(baseDiskGB + students / usersPerAdditionalDiskGb);
        translation.setBandwidth(baseBandwidthMbps + (students / usersPerAdditionalBandwidthGbps) * MB_MULTIPLIER);

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
