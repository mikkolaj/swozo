package com.swozo.util.mock;

import com.swozo.persistence.vminfo.PimVmInfo;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public abstract class ServiceModule {
    protected Long id;
    protected Integer baseVcpu;
    protected Integer baseRam;
    protected Integer baseDisk;
    protected Integer baseBanwidth;

    public abstract PimVmInfo getPimVmInfo(Integer studens);

    public abstract Boolean isIsolated();
}
