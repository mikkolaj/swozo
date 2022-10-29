package com.swozo.persistence.vmInfo;

import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PSMVmInfo {
    private Integer amount;
    private ArrayList<Long> moduleIds = new ArrayList<>();
    private String machine_type;
    private Integer disk;
}
