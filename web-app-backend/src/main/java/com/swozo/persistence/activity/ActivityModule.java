package com.swozo.persistence.activity;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.ServiceModule;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Entity
@Table(name = "ActivityModules")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ActivityModule extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "service_module_id")
    private ServiceModule serviceModule;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    private Long requestId;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "link_id")
    @ToString.Exclude
    private Collection<ActivityLink> links = new ArrayList<>();

    /*
    insert some MDA info fields here
     */

    // not sure about this, we can wrap connectionDetails and instructionHtml in optional instead
    public ActivityModule(ServiceModule serviceModule) {
        this.serviceModule = serviceModule;
    }

    public void addLink(ActivityLink link) {
        links.add(link);
    }

    public Optional<Long> getRequestId() {
        return Optional.ofNullable(requestId);
    }
}
