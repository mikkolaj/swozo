package com.swozo.databasemodel;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@Entity
@Table(name = "ActivityModules")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ActivityModule extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "service_module_id")
    private ServiceModule module;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    private String instruction;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "link_id")
    @ToString.Exclude
    private Collection<ActivityLink> links = new ArrayList<>();

    /*
    insert some MDA info fields here
     */

    // not sure about this, we can wrap connectionDetails and instruction in optional instead
    public ActivityModule(ServiceModule serviceModule) {
        this.module = serviceModule;
    }

    public void addLink(ActivityLink link) {
        links.add(link);
    }
}
