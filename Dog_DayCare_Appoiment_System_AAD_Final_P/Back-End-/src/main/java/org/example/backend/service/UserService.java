package org.example.backend.service;

import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.dto.CreateUserRequest;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.dto.UpdateUserRequest;

import java.util.List;

public interface UserService {
    /** Admin-only: create caretaker / doctor / admin accounts */
    User create(CreateUserRequest request);

    /** Public: self-registration as PET_OWNER */
    User registerPetOwner(RegisterRequest request);

    List<User> getByRole(UserRole role);

    User getById(Long id);

    User update(Long id, UpdateUserRequest request);

    void delete(Long id, Long performedById);
}
