package com.swozo.databasemodel.users;

import com.swozo.databasemodel.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.util.Collection;
import java.util.LinkedList;

@Entity
@Table(
        name = "Users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uc_user_email", columnNames = {"email"})
        }
)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class User extends BaseEntity {
    private String email;
    private String password;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    private Collection<Role> roles = new LinkedList<>();
}

