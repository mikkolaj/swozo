package com.swozo.example;


import com.swozo.model.users.User;
import com.swozo.model.users.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserService {
    private final UserRepository importedRepo;

    @Autowired
    public UserService(UserRepository importedRepo) {
        this.importedRepo = importedRepo;

        importedRepo.save(new User("admin", "admin"));
    }

    public User getUser() {
        return importedRepo.findByEmail("admin").orElseThrow();
    }

    @PostConstruct
    public void init() {
        System.out.println("Here's a user: " + getUser());
    }
}
