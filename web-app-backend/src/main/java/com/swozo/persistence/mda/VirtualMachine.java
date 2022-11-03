package com.swozo.persistence.mda;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "VirtualMachine")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VirtualMachine extends BaseEntity {
    String name;
    Integer vcpu;
    Integer ram;
    Integer bandwidth;
}
