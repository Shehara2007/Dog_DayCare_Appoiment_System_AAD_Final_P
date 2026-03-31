package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.backend.EnumPackage.UserRole;

@Getter
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String name;
    private String email;
    private UserRole role;
}

