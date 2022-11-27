package com.swozo.persistence.servicemodule;

import com.swozo.api.web.servicemodule.dto.ServiceModuleMdaDto;
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
    @Column(columnDefinition="TEXT")
    protected String description;
    @Column(columnDefinition="TEXT")
    protected String teacherInstructionHtml;
    @Column(columnDefinition="TEXT")
    protected String studentInstructionHtml;
    protected String serviceName;
    protected String serviceDisplayName;
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
    protected Integer baseRamGB;
    protected Integer baseDiskGB;
    protected Integer baseBandwidthMbps;

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

    public void setMdaData(ServiceModuleMdaDto mdaData) {
        baseVcpu = mdaData.baseVcpu();
        baseRamGB = mdaData.baseRamGB();
        baseBandwidthMbps = mdaData.baseBandwidthMbps();
        baseDiskGB = mdaData.baseDiskGB();
    }

    @Override
    public String toString(){
        return String.format("ServiceModule(name=%s, subject=%s, description=%s, teacherInstructionHtml=%s," +
                " studentInstructionHtml=%s, serviceName=%s, isPublic = %s, ready=%s, createdAt=%s, dynamicProperites=%s," +
                " creatorId=%s, baseVcpu=%s, baseRamGB=%s, baseDiskGB=%s, baseBandwidthMbps=%s)", name, subject,
                description, teacherInstructionHtml, studentInstructionHtml, serviceName, isPublic, ready, createdAt,
                dynamicProperties, creator.getId(), baseVcpu, baseRamGB, baseDiskGB, baseBandwidthMbps);
    }
}
