package com.swozo.persistence.mda.models;

import com.swozo.persistence.mda.vminfo.PsmVmInfo;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Psm{
    private PsmVmInfo teacherVm;
    private Optional<PsmVmInfo> studentsVms;
}
