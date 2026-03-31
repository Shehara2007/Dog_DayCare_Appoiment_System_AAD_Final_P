package org.example.backend.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.PasswordResetOtp;
import org.example.backend.Entity.User;
import org.example.backend.Repository.PasswordResetOtpRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.exeception.BusinessException;
import org.example.backend.service.PasswordResetService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private final UserRepository userRepository;
    private final PasswordResetOtpRepository otpRepository;
    private final PasswordEncoder passwordEncoder;
    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${app.mail.from:${spring.mail.username:}}")
    private String fromEmail;

    @Value("${app.auth.forgot-password.otp-expiry-minutes:10}")
    private long otpExpiryMinutes;

    @Override
    @Transactional
    public void sendOtp(String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null || !user.isActive()) {
            return;
        }

        String otp = generateOtp();

        otpRepository.deleteByEmail(email);

        PasswordResetOtp token = new PasswordResetOtp();
        token.setEmail(email);
        token.setOtpCode(otp);
        token.setExpiresAt(LocalDateTime.now().plusMinutes(otpExpiryMinutes));
        otpRepository.save(token);

        sendOtpEmail(user, otp);
    }

    @Override
    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        PasswordResetOtp token = otpRepository
                .findTopByEmailAndOtpCodeAndUsedFalseOrderByCreatedAtDesc(email, otp)
                .orElseThrow(() -> new BusinessException("Invalid OTP"));

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("OTP has expired. Please request a new OTP.");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Invalid reset request"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        token.setUsed(true);
        otpRepository.save(token);
    }

    private String generateOtp() {
        int value = ThreadLocalRandom.current().nextInt(100000, 1000000);
        return String.valueOf(value);
    }

    private void sendOtpEmail(User user, String otp) {
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null || !StringUtils.hasText(user.getEmail())) {
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setTo(user.getEmail());
            if (StringUtils.hasText(fromEmail)) {
                helper.setFrom(fromEmail.trim());
            }
            helper.setSubject("PawCare Password Reset OTP");
            helper.setText(buildTextBody(user.getName(), otp));
            mailSender.send(message);
        } catch (MessagingException ex) {
            throw new BusinessException("Failed to send OTP email");
        }
    }

    private String buildTextBody(String name, String otp) {
        return "Hello " + (StringUtils.hasText(name) ? name : "User") + ",\n\n"
                + "Use the OTP below to reset your PawCare account password:\n"
                + otp + "\n\n"
                + "This OTP expires in " + otpExpiryMinutes + " minutes.\n"
                + "If you did not request this, please ignore this email.\n\n"
                + "- PawCare Team";
    }
}

