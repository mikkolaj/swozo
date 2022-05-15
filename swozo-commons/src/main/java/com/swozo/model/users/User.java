package com.swozo.model.users;

import com.swozo.model.BaseEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "Users")
@NoArgsConstructor
@Getter
@Setter
@ToString
public class User extends BaseEntity {
    private String name;

    public User(String name) {
        this.name = name;
    }
}

