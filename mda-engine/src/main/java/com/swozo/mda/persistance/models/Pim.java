package com.swozo.mda.persistance.models;

import com.swozo.mda.persistance.vmInfo.PIMVmInfo;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Pim{
    private PIMVmInfo teacherVm;
    private Optional<PIMVmInfo> studentsVms = Optional.empty();

}
