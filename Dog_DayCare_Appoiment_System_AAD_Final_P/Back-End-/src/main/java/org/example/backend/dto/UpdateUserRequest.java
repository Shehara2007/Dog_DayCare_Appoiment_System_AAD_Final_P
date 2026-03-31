package org.example.backend.dto;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.EnumPackage.UserRole;

@Getter
@Setter
public class UpdateUserRequest {

    private String name;

    @Email
    private String email;

    private String phone;

    private String password;

    private UserRole role;

    private Boolean active;
}

