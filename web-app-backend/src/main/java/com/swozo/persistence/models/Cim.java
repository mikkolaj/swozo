package com.swozo.persistence.models;

import com.swozo.util.mock.ServiceModule;
import lombok.*;

import java.util.ArrayList;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Cim{
    private ArrayList<ServiceModule> selectedModules = new ArrayList<>();
    private Long userId;
    private Integer studentsNumber;
}
