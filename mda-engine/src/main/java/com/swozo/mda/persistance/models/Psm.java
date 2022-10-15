package com.swozo.mda.persistance.models;

import com.swozo.mda.persistance.vmInfo.PSMVmInfo;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Psm{
    private PSMVmInfo teacherVm;
    private Optional<PSMVmInfo> studentsVms;
}
