package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.User;
import org.example.backend.dto.AuthResponse;
import org.example.backend.dto.ForgotPasswordRequest;
import org.example.backend.dto.LoginRequest;
import org.example.backend.dto.ApiMessageResponse;
import org.example.backend.dto.RegisterRequest;
import org.example.backend.dto.ResetPasswordWithOtpRequest;
import org.example.backend.security.JwtUtils;
import org.example.backend.security.UserDetailsServiceImpl;
import org.example.backend.service.PasswordResetService;
import org.example.backend.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtUtils jwtUtils;
    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        User user = userService.registerPetOwner(request);
        String token = jwtUtils.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole()));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) userDetailsService.loadUserByUsername(request.getEmail());
        String token = jwtUtils.generateToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getName(), user.getEmail(), user.getRole()));
    }

    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<ApiMessageResponse> requestPasswordOtp(@Valid @RequestBody ForgotPasswordRequest request) {
        passwordResetService.sendOtp(request.getEmail());
        return ResponseEntity.ok(new ApiMessageResponse("If the email exists, an OTP has been sent."));
    }

    @PostMapping("/forgot-password/reset")
    public ResponseEntity<ApiMessageResponse> resetPassword(@Valid @RequestBody ResetPasswordWithOtpRequest request) {
        passwordResetService.resetPassword(request.getEmail(), request.getOtp(), request.getNewPassword());
        return ResponseEntity.ok(new ApiMessageResponse("Password has been reset successfully."));
    }
}

