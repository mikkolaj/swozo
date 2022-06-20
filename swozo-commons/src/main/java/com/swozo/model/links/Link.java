package com.swozo.model.links;

import com.swozo.databasemodel.BaseEntity;
import lombok.*;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Link extends BaseEntity {
    private String link;
    private String description;

//    TODO consider replaceing this to databasemodel package
}
