package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.dto.CreateUserRequest;
import org.example.backend.dto.UpdateUserRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody CreateUserRequest request) {
        User user = userService.create(request);
        return mapUser(user);
    }

    @GetMapping
    public List<Map<String, Object>> list(@RequestParam(required = false) UserRole role) {
        return userService.getByRole(role).stream().map(this::mapUser).toList();
    }

    @GetMapping("/public/by-role")
    public List<Map<String, Object>> listPublicByRole(@RequestParam UserRole role) {
        if (role != UserRole.DOCTOR && role != UserRole.CARETAKER) {
            throw new BusinessException("Public access is allowed only for DOCTOR or CARETAKER roles");
        }
        return userService.getByRole(role).stream().map(this::mapUser).toList();
    }

    @GetMapping("/{id}")
    public Map<String, Object> getById(@PathVariable Long id) {
        return mapUser(userService.getById(id));
    }

    @PutMapping("/{id}")
    public Map<String, Object> update(@PathVariable Long id, @Valid @RequestBody UpdateUserRequest request) {
        return mapUser(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id, @AuthenticationPrincipal User currentUser) {
        userService.delete(id, currentUser != null ? currentUser.getId() : null);
        return Map.of("message", "User deleted successfully", "userId", id);
    }

    private Map<String, Object> mapUser(User user) {
        return Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "phone", user.getPhone(),
                "role", user.getRole(),
                "active", user.isActive()
        );
    }
}


