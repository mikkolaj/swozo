package com.swozo.persistence.mda;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "VirtualMachines")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class VirtualMachine extends BaseEntity {
    private String name;
    private Integer vcpu;
    private Integer ramGB;
    private Integer bandwidthMbps;
    private Integer imageDiskSizeGB;
    @Column(columnDefinition="TEXT")
    private String descriptionHtml;
}
