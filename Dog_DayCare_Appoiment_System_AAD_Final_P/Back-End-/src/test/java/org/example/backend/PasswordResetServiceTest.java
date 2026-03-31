package org.example.backend;

import org.example.backend.Entity.PasswordResetOtp;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.Repository.PasswordResetOtpRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.dto.CreateUserRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.service.PasswordResetService;
import org.example.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class PasswordResetServiceTest {

    @Autowired private PasswordResetService passwordResetService;
    @Autowired private UserService userService;
    @Autowired private UserRepository userRepository;
    @Autowired private PasswordResetOtpRepository otpRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Test
    void shouldResetPasswordUsingValidOtp() {
        String email = "otp-valid@test.com";
        createPetOwner("Otp User", email, "OldPass@123");

        passwordResetService.sendOtp(email);

        PasswordResetOtp otp = otpRepository.findAll().stream()
                .filter(item -> email.equals(item.getEmail()) && !item.isUsed())
                .findFirst()
                .orElseThrow();

        passwordResetService.resetPassword(email, otp.getOtpCode(), "NewPass@123");

        User updated = userRepository.findByEmail(email).orElseThrow();
        assertTrue(passwordEncoder.matches("NewPass@123", updated.getPassword()));
    }

    @Test
    void shouldRejectExpiredOtp() {
        String email = "otp-expired@test.com";
        createPetOwner("Otp Expired", email, "OldPass@123");

        PasswordResetOtp otp = new PasswordResetOtp();
        otp.setEmail(email);
        otp.setOtpCode("123456");
        otp.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        otpRepository.save(otp);

        assertThrows(BusinessException.class, () ->
                passwordResetService.resetPassword(email, "123456", "NewPass@123"));
    }

    private void createPetOwner(String name, String email, String password) {
        CreateUserRequest req = new CreateUserRequest();
        req.setName(name);
        req.setEmail(email);
        req.setPhone("0770000000");
        req.setPassword(password);
        req.setRole(UserRole.PET_OWNER);
        userService.create(req);
    }
}


