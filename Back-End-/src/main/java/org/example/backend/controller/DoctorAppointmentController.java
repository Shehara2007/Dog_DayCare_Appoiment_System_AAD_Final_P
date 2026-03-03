package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.dto.AppointmentDTOs;
import org.example.backend.service.DoctorAppointmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctor-appointments")
@RequiredArgsConstructor
public class DoctorAppointmentController {

    private final DoctorAppointmentService doctorAppointmentService;

    @PostMapping
    public ResponseEntity<AppointmentDTOs.DoctorAppointmentResponse> bookAppointment(
            @Valid @RequestBody AppointmentDTOs.DoctorAppointmentRequest request) {
        return ResponseEntity.ok(doctorAppointmentService.bookAppointment(request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AppointmentDTOs.DoctorAppointmentResponse>> getMyAppointments() {
        return ResponseEntity.ok(doctorAppointmentService.getMyDoctorAppointments());
    }

    @GetMapping("/doctor/schedule")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<List<AppointmentDTOs.DoctorAppointmentResponse>> getDoctorSchedule() {
        return ResponseEntity.ok(doctorAppointmentService.getDoctorSchedule());
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<AppointmentDTOs.DoctorAppointmentResponse> updateStatus(
            @PathVariable Long id,
            @RequestParam AppointmentStatus status,
            @RequestParam(required = false) String prescription,
            @RequestParam(required = false) String diagnosis) {
        return ResponseEntity.ok(doctorAppointmentService.updateStatus(id, status, prescription, diagnosis));
    }
}