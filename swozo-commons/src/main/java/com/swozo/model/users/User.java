package com.swozo.model.users;

import com.swozo.model.BaseEntity;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "Users", indexes = {
        @Index(name = "idx_user_email", columnList = "email")
}, uniqueConstraints = {
        @UniqueConstraint(name = "uc_user_email", columnNames = {"email"})
})
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User extends BaseEntity {
    private String email;
    private String password;
}

