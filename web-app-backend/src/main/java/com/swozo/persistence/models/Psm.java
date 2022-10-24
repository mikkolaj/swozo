package com.swozo.persistence.models;

import com.swozo.persistence.vmInfo.PSMVmInfo;
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
