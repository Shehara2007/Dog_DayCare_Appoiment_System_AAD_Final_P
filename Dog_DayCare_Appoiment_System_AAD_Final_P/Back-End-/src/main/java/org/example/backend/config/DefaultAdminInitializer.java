package org.example.backend.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DefaultAdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.enabled:false}")
    private boolean bootstrapAdminEnabled;

    @Value("${app.bootstrap.admin.name:Default Admin}")
    private String name;

    @Value("${app.bootstrap.admin.email:}")
    private String email;

    @Value("${app.bootstrap.admin.phone:0000000000}")
    private String phone;

    @Value("${app.bootstrap.admin.password:}")
    private String password;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!bootstrapAdminEnabled) {
            log.info("Bootstrap admin creation is disabled.");
            return;
        }

        if (userRepository.findByRole(UserRole.ADMIN).stream().findAny().isPresent()) {
            log.info("Admin user already exists. Skipping bootstrap admin creation.");
            return;
        }

        String resolvedEmail = email == null ? "" : email.trim();
        String resolvedPassword = password == null ? "" : password.trim();
        String resolvedName = (name == null || name.trim().isEmpty()) ? "Default Admin" : name.trim();
        String resolvedPhone = (phone == null || phone.trim().isEmpty()) ? "0000000000" : phone.trim();

        if (resolvedEmail.isEmpty() || resolvedPassword.isEmpty()) {
            log.warn("Bootstrap admin email or password is empty. Skipping bootstrap admin creation.");
            return;
        }

        if (userRepository.findByEmail(resolvedEmail).isPresent()) {
            log.warn("Bootstrap admin email '{}' already exists. Skipping bootstrap admin creation.", resolvedEmail);
            return;
        }

        User adminUser = new User();
        adminUser.setName(resolvedName);
        adminUser.setEmail(resolvedEmail);
        adminUser.setPhone(resolvedPhone);
        adminUser.setPassword(passwordEncoder.encode(resolvedPassword));
        adminUser.setRole(UserRole.ADMIN);
        adminUser.setActive(true);

        userRepository.save(adminUser);
        log.warn("Default admin created with email '{}'. Change this password immediately.", resolvedEmail);
    }
}

