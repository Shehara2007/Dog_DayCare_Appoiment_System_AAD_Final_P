package org.example.backend.service;

public interface PasswordResetService {
    void sendOtp(String email);

    void resetPassword(String email, String otp, String newPassword);
}

