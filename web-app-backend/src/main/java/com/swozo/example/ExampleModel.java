package com.swozo.example;

import com.swozo.databasemodel.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "example")
@NoArgsConstructor
@Getter
@Setter
@ToString
// @Data w entity może namieszać: https://deinum.biz/2019-02-13-Lombok-Data-Ojects-Arent-Entities/
// Problematyczne są ToString i HashCode, można rozwiązać dając swój HashCode i usuwając
// niepotrzebne pola z @ToStringa
public class ExampleModel extends BaseEntity {
    private String name;

    // uznajmy, że jest to relacja z inną tabelą
    // wtedy trzeba wykluczyć ją z ToStringa, bo będzie querować tamte dane
    @ToString.Exclude
    private String someBigCollection;

    public ExampleModel(String name) {
        this.name = name;
    }
}
