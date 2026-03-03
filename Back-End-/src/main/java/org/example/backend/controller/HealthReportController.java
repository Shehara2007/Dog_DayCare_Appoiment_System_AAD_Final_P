package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.AppointmentDTOs;
import org.example.backend.service.HealthReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/health-reports")
@RequiredArgsConstructor
public class HealthReportController {

    private final HealthReportService healthReportService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CARETAKER')")
    public ResponseEntity<AppointmentDTOs.HealthReportResponse> createReport(@Valid @RequestBody AppointmentDTOs.HealthReportRequest request) {
        return ResponseEntity.ok(healthReportService.createReport(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDTOs.HealthReportResponse>> getAllReports() {
        return ResponseEntity.ok(healthReportService.getAllReports());
    }
}