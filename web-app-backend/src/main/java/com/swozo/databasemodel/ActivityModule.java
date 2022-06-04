package com.swozo.databasemodel;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "activity_id")
    private Activity activity;

    private String instruction;

    @ElementCollection
    private List<String> links = new ArrayList<>();

    /*
    insert some MDA info fields here
     */

    public void addLink(String link) {
        links.add(link);
    }
}
