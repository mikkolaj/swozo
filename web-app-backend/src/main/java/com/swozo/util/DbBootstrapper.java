package com.swozo.util;

import com.swozo.api.auth.dto.AppRole;
import com.swozo.model.users.Role;
import com.swozo.model.users.User;
import com.swozo.repository.RoleRepository;
import com.swozo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

@Component
public class DbBootstrapper implements ApplicationListener<ContextRefreshedEvent> {
    private final Logger logger = LoggerFactory.getLogger(DbBootstrapper.class);

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private boolean alreadySetup;

    @Autowired
    public DbBootstrapper(RoleRepository roleRepository, UserRepository userRepository) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.alreadySetup = false;
    }

    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent event) {
        // see https://www.baeldung.com/role-and-privilege-for-spring-security-registration for alreadySetup motivation
        if (this.alreadySetup)
            return;
        logger.info("preparing database...");

        prepareRoles();
        setupTestUsers();

        logger.info("database ready");
        alreadySetup = true;
    }

    private void prepareRoles() {
        Arrays.stream(AppRole.values())
                .map(AppRole::toString)
                .filter(name -> roleRepository.findByName(name) == null)
                .forEach(name -> roleRepository.save(new Role(name)));
    }

    // TODO assert dev env
    private void setupTestUsers() {
        var adminRole = roleRepository.findByName(AppRole.ADMIN.toString());
        userRepository.save(new User("admin", "admin", List.of(adminRole)));

        var teacherRole = roleRepository.findByName(AppRole.TEACHER.toString());
        var technicalTeacherRole = roleRepository.findByName(AppRole.TECHNICAL_TEACHER.toString());
        userRepository.save(new User("teacher", "teacher", List.of(teacherRole, technicalTeacherRole)));
    }
}
