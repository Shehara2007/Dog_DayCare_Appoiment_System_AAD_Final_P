package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.security.JwtUtil;
import org.example.backend.Repository.UserRepository;
import org.example.backend.Entity.User;
import org.example.backend.dto.AuthDTOs;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public AuthDTOs.AuthResponse register(AuthDTOs.RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .phone(request.getPhone())
                .address(request.getAddress())
                .role(request.getRole())
                .maxDogCapacity(request.getMaxDogCapacity() != null ? request.getMaxDogCapacity() : 3)
                .specialization(request.getSpecialization())
                .licenseNumber(request.getLicenseNumber())
                .build();

        userRepository.save(user);
        String token = jwtUtil.generateToken(user);
        return new AuthDTOs.AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    public AuthDTOs.AuthResponse login(AuthDTOs.LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));
        String token = jwtUtil.generateToken(user);
        return new AuthDTOs.AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }
}