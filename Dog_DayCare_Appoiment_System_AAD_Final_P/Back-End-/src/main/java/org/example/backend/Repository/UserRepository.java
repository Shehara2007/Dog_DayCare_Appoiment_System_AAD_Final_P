package org.example.backend.Repository;

import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByRole(UserRole role);
    Optional<User> findByEmail(String email);
}

