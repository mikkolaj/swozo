package com.swozo.persistence;

import com.swozo.persistence.user.User;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "ServiceModules")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ServiceModule extends BaseEntity {
    private String name;
    private String subject;
    private String description;
    private String teacherInstructionHtml;
    private String studentInstructionHtml;
    private String scheduleTypeName;
    private Boolean isPublic;
    private Boolean ready;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection
    @MapKeyColumn(name = "property_name")
    @Column(name = "property_value")
    @CollectionTable(name = "service_module_dynamic_properties", joinColumns = @JoinColumn(name = "service_module_id"))
    private Map<String, String> dynamicProperties = new HashMap<>();

    @ManyToOne(fetch = FetchType.EAGER)
    private User creator;

    /*
    insert some service specs here
     */

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public boolean isReady() {
        return ready;
    }
}
