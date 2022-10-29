package com.swozo.api.web.user;

import com.swozo.persistence.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    User getByEmail(String email);
}
