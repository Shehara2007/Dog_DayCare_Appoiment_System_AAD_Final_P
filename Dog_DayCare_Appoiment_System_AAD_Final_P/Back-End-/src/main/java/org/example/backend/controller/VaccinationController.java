package org.example.backend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Vaccination;
import org.example.backend.dto.CreateVaccinationRequest;
import org.example.backend.service.VaccinationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/vaccinations")
@RequiredArgsConstructor
public class VaccinationController {

    private final VaccinationService vaccinationService;

    @PostMapping
    public Map<String, Object> create(@Valid @RequestBody CreateVaccinationRequest request) {
        return mapVaccination(vaccinationService.create(request));
    }

    @GetMapping
    public List<Map<String, Object>> getByDog(@RequestParam Long dogId) {
        return vaccinationService.getByDog(dogId).stream().map(this::mapVaccination).toList();
    }

    private Map<String, Object> mapVaccination(Vaccination vaccination) {
        return Map.of(
                "id", vaccination.getId(),
                "dogId", vaccination.getDog().getId(),
                "vaccineName", vaccination.getVaccineName(),
                "givenDate", vaccination.getGivenDate(),
                "expiryDate", vaccination.getExpiryDate(),
                "alertSent", vaccination.isAlertSent()
        );
    }
}

