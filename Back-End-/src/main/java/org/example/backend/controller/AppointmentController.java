package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.dto.AppointmentDTOs;
import org.example.backend.service.AppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDTOs.AppointmentResponse> bookAppointment(@Valid @RequestBody AppointmentDTOs.AppointmentRequest request) {
        return ResponseEntity.ok(appointmentService.bookAppointment(request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentDTOs.AppointmentResponse>> getMyAppointments() {
        return ResponseEntity.ok(appointmentService.getMyAppointments());
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDTOs.AppointmentResponse>> getAllAppointments() {
        return ResponseEntity.ok(appointmentService.getAllAppointments());
    }

    @GetMapping("/caretaker")
    @PreAuthorize("hasAnyRole('ADMIN', 'CARETAKER')")
    public ResponseEntity<List<AppointmentDTOs.AppointmentResponse>> getCaretakerAppointments() {
        return ResponseEntity.ok(appointmentService.getCaretakerAppointments());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'CARETAKER')")
    public ResponseEntity<AppointmentDTOs.AppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(appointmentService.updateStatus(id, status));
    }
}