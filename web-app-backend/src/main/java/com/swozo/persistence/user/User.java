package com.swozo.persistence.user;

import com.swozo.persistence.BaseEntity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

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
    private String name;
    private String surname;
    private String email;
    private String password;
    private String changePasswordToken;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    private Collection<Role> roles = new LinkedList<>();

    public User(String name, String surname, String email, String password, List<Role> roles) {
        this.name = name;
        this.surname= surname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }
}
