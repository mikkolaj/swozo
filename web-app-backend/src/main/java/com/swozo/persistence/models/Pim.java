package com.swozo.persistence.models;

import com.swozo.persistence.vmInfo.PIMVmInfo;
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
