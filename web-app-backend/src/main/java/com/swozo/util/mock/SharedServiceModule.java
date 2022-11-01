package com.swozo.util.mock;

import com.swozo.persistence.vminfo.PimVmInfo;
import lombok.*;

@Getter
@Setter
@ToString
public class SharedServiceModule extends ServiceModule{
    private Integer usersPerAdditionalCore;
    private Integer usersPerAdditionalRamGb;
    private Integer usersPerAdditionalDiskGb;
    private Integer usersPerAdditionalBandiwthGbps;
    private static Boolean isolated = false;

    public SharedServiceModule(Long id, Integer baseVcpu, Integer baseRam, Integer baseDisk, Integer baseBanwidth,
                                 Integer usersPerAdditionalCore, Integer usersPerAdditionalRamGb,
                                 Integer usersPerAdditionalDiskGb, Integer usersPerAdditionalBandiwthGbps){
        super(id,baseVcpu, baseRam, baseDisk, baseBanwidth);
        this.usersPerAdditionalCore = usersPerAdditionalCore;
        this.usersPerAdditionalRamGb = usersPerAdditionalRamGb;
        this.usersPerAdditionalDiskGb = usersPerAdditionalDiskGb;
        this.usersPerAdditionalBandiwthGbps = usersPerAdditionalBandiwthGbps;
    }

    public PimVmInfo getPimVmInfo(Integer studens){
        PimVmInfo translation = new PimVmInfo();
        translation.addModule(id);
        translation.setVCPUs(baseVcpu + studens / usersPerAdditionalCore);
        translation.setRam(baseRam + studens / usersPerAdditionalRamGb);
        translation.setDisk(baseDisk + studens / usersPerAdditionalDiskGb);
        translation.setBandiwth(baseBanwidth + studens / usersPerAdditionalBandiwthGbps);

        return translation;
    }

    @Override
    public Boolean isIsolated() {
        return false;
    }
}
