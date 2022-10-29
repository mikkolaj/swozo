package com.swozo.persistence.models;

import com.swozo.persistence.vmInfo.PimVmInfo;
import lombok.*;

import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Pim{
    private PimVmInfo teacherVm;
    private Optional<PimVmInfo> studentsVms = Optional.empty();

}
