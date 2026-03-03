package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.dto.AppointmentDTOs;
import org.example.backend.dto.DogDTOs;
import org.example.backend.service.DogService;
import org.example.backend.service.HealthReportService;
import org.example.backend.service.VaccinationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dogs")
@RequiredArgsConstructor
public class DogController {

    private final DogService dogService;
    private final HealthReportService healthReportService;
    private final VaccinationService vaccinationService;

    @PostMapping
    public ResponseEntity<DogDTOs.DogResponse> registerDog(@Valid @RequestBody DogDTOs.DogRequest request) {
        return ResponseEntity.ok(dogService.registerDog(request));
    }

    @GetMapping("/my")
    public ResponseEntity<List<DogDTOs.DogResponse>> getMyDogs() {
        return ResponseEntity.ok(dogService.getMyDogs());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DogDTOs.DogResponse> getDogById(@PathVariable Long id) {
        return ResponseEntity.ok(dogService.getDogById(id));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DogDTOs.DogResponse>> getAllDogs() {
        return ResponseEntity.ok(dogService.getAllDogs());
    }

    @PutMapping("/{id}")
    public ResponseEntity<DogDTOs.DogResponse> updateDog(@PathVariable Long id, @Valid @RequestBody DogDTOs.DogRequest request) {
        return ResponseEntity.ok(dogService.updateDog(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> deleteDog(@PathVariable Long id) {
        dogService.deleteDog(id);
        return ResponseEntity.ok(Map.of("message", "Dog deleted successfully"));
    }

    // Health Reports
    @GetMapping("/{id}/health-reports")
    public ResponseEntity<List<AppointmentDTOs.HealthReportResponse>> getDogReports(@PathVariable Long id) {
        return ResponseEntity.ok(healthReportService.getDogReports(id));
    }

    // Vaccinations
    @GetMapping("/{id}/vaccinations")
    public ResponseEntity<List<AppointmentDTOs.VaccinationResponse>> getDogVaccinations(@PathVariable Long id) {
        return ResponseEntity.ok(vaccinationService.getDogVaccinations(id));
    }

    // Public QR endpoint
    @GetMapping("/{id}/qr-public")
    public ResponseEntity<Map<String, Object>> getPublicDogInfo(@PathVariable Long id) {
        DogDTOs.DogResponse dog = dogService.getDogById(id);
        List<AppointmentDTOs.HealthReportResponse> reports = healthReportService.getDogReports(id);
        List<AppointmentDTOs.VaccinationResponse> vaccinations = vaccinationService.getDogVaccinations(id);
        return ResponseEntity.ok(Map.of(
                "dog", dog,
                "healthReports", reports,
                "vaccinations", vaccinations
        ));
    }
}