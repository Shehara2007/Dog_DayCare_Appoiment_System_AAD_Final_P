package org.example.backend.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.Repository.AppointmentRepository;
import org.example.backend.Repository.DogRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.Entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final DogRepository dogRepository;
    private final AppointmentRepository appointmentRepository;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboard() {
        return ResponseEntity.ok(Map.of(
                "totalUsers", userRepository.count(),
                "totalDogs", dogRepository.count(),
                "totalAppointments", appointmentRepository.count(),
                "caretakers", userRepository.findByRole(UserRole.CARETAKER).size(),
                "doctors", userRepository.findByRole(UserRole.DOCTOR).size(),
                "owners", userRepository.findByRole(UserRole.OWNER).size()
        ));
    }

    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    @GetMapping("/caretakers")
    public ResponseEntity<List<User>> getCaretakers() {
        return ResponseEntity.ok(userRepository.findByRole(UserRole.CARETAKER));
    }

    @GetMapping("/doctors")
    public ResponseEntity<List<User>> getDoctors() {
        return ResponseEntity.ok(userRepository.findByRole(UserRole.DOCTOR));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Map<String, String>> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
    }

    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<User> toggleUserStatus(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        return ResponseEntity.ok(userRepository.save(user));
    }
}