package com.swozo.databasemodel;

import com.swozo.model.links.Link;
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
    private Collection<Link> links = new ArrayList<>();

    /*
    insert some MDA info fields here
     */

    public void addLink(Link link) {
        links.add(link);
    }
}
