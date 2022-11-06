package com.swozo.persistence.servicemodule;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.mda.vminfo.PimVmInfo;
import com.swozo.persistence.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "ServiceModules")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public abstract class ServiceModule extends BaseEntity {
    protected String name;
    protected String subject;
    protected String description;
    protected String teacherInstructionHtml;
    protected String studentInstructionHtml;
    protected String serviceName;
    protected Boolean isPublic;
    protected Boolean ready;

    protected LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value")
    @CollectionTable(name = "service_module_dynamic_properties", joinColumns = @JoinColumn(name = "service_module_id"))
    protected Map<String, String> dynamicProperties = new HashMap<>();

    @ManyToOne(fetch = FetchType.EAGER)
    protected User creator;

    protected Integer baseVcpu;
    protected Integer baseRam;
    protected Integer baseDisk;
    protected Integer baseBandwidth;

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isReady() {
        return ready;
    }

    public abstract PimVmInfo getPimVmInfo(Integer students);

    public abstract Boolean isIsolated();
}
