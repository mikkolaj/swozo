package com.swozo.example;

import com.swozo.databasemodel.users.User;
import com.swozo.repository.RoleRepository;
import com.swozo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository importedRepo;
    private final RoleRepository roleRepository;

    @Autowired
    public UserService(UserRepository importedRepo, RoleRepository roleRepository) {
        this.importedRepo = importedRepo;
        this.roleRepository = roleRepository;
//
//        var roles = List.of(roleRepository.findByName(AppRole.ADMIN.toString()));
//        importedRepo.save(new User("admin", "admin", roles));
    }

    public User getUser() {
        return importedRepo.findByEmail("admin").orElseThrow();
    }
}
