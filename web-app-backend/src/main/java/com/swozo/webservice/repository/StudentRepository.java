package com.swozo.webservice.repository;

import com.swozo.databasemodel.users.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<User, Long> {
}
