package com.swozo.databasemodel;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ModuleMetadata")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class ModuleMetadata extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "module_id")
    private Module module;

    private String instruction;

    @ElementCollection
    private List<String> links = new ArrayList<>();

    public void addLink(String link) {
        links.add(link);
    }
}
