package com.swozo.example;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "example")
public class ExampleModel {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    public ExampleModel() {
    }

    public ExampleModel(String name) {
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExampleModel)) return false;
        ExampleModel that = (ExampleModel) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return "ExampleModel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
