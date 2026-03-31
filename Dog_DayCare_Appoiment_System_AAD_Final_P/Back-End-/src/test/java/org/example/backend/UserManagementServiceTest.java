package org.example.backend;

import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.dto.CreateUserRequest;
import org.example.backend.dto.UpdateUserRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.exeception.ResourceNotFoundException;
import org.example.backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class UserManagementServiceTest {

    @Autowired private UserService userService;

    @Test
    void shouldUpdateAndDeleteUser() {
        User created = createUser("Edit Me", "edit-user@test.com", UserRole.CARETAKER);

        UpdateUserRequest update = new UpdateUserRequest();
        update.setName("Edited User");
        update.setEmail("edited-user@test.com");
        update.setPhone("0711111111");
        update.setRole(UserRole.DOCTOR);
        update.setActive(false);

        User updated = userService.update(created.getId(), update);

        assertEquals("Edited User", updated.getName());
        assertEquals("edited-user@test.com", updated.getEmail());
        assertEquals("0711111111", updated.getPhone());
        assertEquals(UserRole.DOCTOR, updated.getRole());
        assertFalse(updated.isActive());

        userService.delete(created.getId(), 9999L);
        assertThrows(ResourceNotFoundException.class, () -> userService.getById(created.getId()));
    }

    @Test
    void shouldPreventSelfDelete() {
        User created = createUser("Self Admin", "self-admin@test.com", UserRole.ADMIN);
        assertThrows(BusinessException.class, () -> userService.delete(created.getId(), created.getId()));
    }

    private User createUser(String name, String email, UserRole role) {
        CreateUserRequest req = new CreateUserRequest();
        req.setName(name);
        req.setEmail(email);
        req.setPhone("0700000000");
        req.setPassword("Temp@1234");
        req.setRole(role);
        return userService.create(req);
    }
}


