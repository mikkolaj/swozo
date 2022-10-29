package com.swozo.persistence.models;

import com.swozo.persistence.vminfo.PsmVmInfo;
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
