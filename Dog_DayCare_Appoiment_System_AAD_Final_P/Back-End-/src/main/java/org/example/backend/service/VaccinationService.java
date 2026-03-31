package org.example.backend.service;

import org.example.backend.Entity.Vaccination;
import org.example.backend.dto.CreateVaccinationRequest;

import java.util.List;

public interface VaccinationService {
    Vaccination create(CreateVaccinationRequest request);
    List<Vaccination> getByDog(Long dogId);
    void processDailyVaccinationAlerts();
}
