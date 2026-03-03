package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.AppointmentDTOs;
import org.example.backend.service.VaccinationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vaccinations")
@RequiredArgsConstructor
public class VaccinationController {

    private final VaccinationService vaccinationService;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'CARETAKER')")
    public ResponseEntity<AppointmentDTOs.VaccinationResponse> addVaccination(@Valid @RequestBody AppointmentDTOs.VaccinationRequest request) {
        return ResponseEntity.ok(vaccinationService.addVaccination(request));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<AppointmentDTOs.VaccinationResponse>> getAllVaccinations() {
        return ResponseEntity.ok(vaccinationService.getAllVaccinations());
    }
}