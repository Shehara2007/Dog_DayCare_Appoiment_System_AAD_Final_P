package org.example.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.example.backend.EnumPackage.UserRole;

// ===== AUTH DTOs =====
public class AuthDTOs {

    @Data
    public static class RegisterRequest {
        @NotBlank
        private String fullName;
        @Email
        @NotBlank private String email;
        @NotBlank private String password;
        private String phone;
        private String address;
        @NotNull
        private UserRole role;
        // Caretaker fields
        private Integer maxDogCapacity;
        // Doctor fields
        private String specialization;
        private String licenseNumber;
    }

    @Data
    public static class LoginRequest {
        @Email @NotBlank private String email;
        @NotBlank private String password;
    }

    @Data
    public static class AuthResponse {
        private String token;
        private String email;
        private String fullName;
        private String role;

        public AuthResponse(String token, String email, String fullName, String role) {
            this.token = token;
            this.email = email;
            this.fullName = fullName;
            this.role = role;
        }
    }
}