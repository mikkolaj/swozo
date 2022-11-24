package com.swozo.persistence.user;

import com.swozo.persistence.BaseEntity;
import com.swozo.persistence.RemoteFile;
import com.swozo.persistence.activity.Activity;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

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
    @ToString.Exclude
    private String password;
    @ToString.Exclude
    private String changePasswordToken;
    private LocalDateTime createdAt = LocalDateTime.now();
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
            joinColumns = @JoinColumn(name = "id", referencedColumnName = "id"))
    private Collection<Role> roles = new LinkedList<>();

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<UserFavouriteFile> favouriteFiles = new LinkedList<>();

    public User(String name, String surname, String email, String password, List<Role> roles) {
        this.name = name;
        this.surname= surname;
        this.email = email;
        this.password = password;
        this.roles = roles;
    }

    public void addRole(Role role) {
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void addFavouriteFile(RemoteFile remoteFile, Activity activity) {
        this.favouriteFiles.add(new UserFavouriteFile(this, remoteFile, activity));
    }

    public Optional<UserFavouriteFile> getUserFavouriteFile(Long fileId) {
       return favouriteFiles.stream()
               .filter(userFavouriteFile -> userFavouriteFile.getRemoteFile().getId().equals(fileId))
               .findAny();
    }

    public void removeFavouriteFile(UserFavouriteFile userFavouriteFile) {
           favouriteFiles.remove(userFavouriteFile);
           userFavouriteFile.setUser(null);
    }
}
