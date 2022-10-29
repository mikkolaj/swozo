package com.swozo.util.mock;

import com.swozo.persistence.vminfo.PimVmInfo;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
//    TODO in next task change serviceModule to this...
public class ModuleMock {
    private Long id;
    private Integer baseVcpu;
    private Integer baseRam;
    private Integer baseDisk;
    private Integer baseBanwidth;
    private Integer usersPerAdditionalCore;
    private Integer usersPerAdditionalRamGb;
    private Integer usersPerAdditionalDiskGb;
    private Integer usersPerAdditionalBandiwthGbps;
    private Boolean isolated;

    public PimVmInfo getPimVmInfo(Integer vmAmount){
        PimVmInfo translation = new PimVmInfo();
        Integer additionalVms = vmAmount - 1;
        translation.addModule(this.getId());
        translation.setVCPUs(this.getBaseVcpu() + additionalVms / this.getUsersPerAdditionalCore());
        translation.setRam(this.getBaseRam() + additionalVms / this.getUsersPerAdditionalRamGb());
        translation.setDisk(this.getBaseDisk() + additionalVms / this.getUsersPerAdditionalDiskGb());
        translation.setBandiwth(this.getBaseBanwidth() + additionalVms / this.getUsersPerAdditionalBandiwthGbps());

        return translation;
    }
}
