package com.swozo.persistence.mda.vminfo;

import com.swozo.persistence.servicemodule.ServiceModule;
import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class PsmVmInfo {
    private Integer amount;
    private ArrayList<ServiceModule> serviceModules = new ArrayList<>();
    private String machine_type;
    private Integer disk;
}
