package com.swozo.mda.util.mock;

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
}
