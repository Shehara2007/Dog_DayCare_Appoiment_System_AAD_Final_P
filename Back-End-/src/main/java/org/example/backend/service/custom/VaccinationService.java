package org.example.backend.service.custom;

import org.example.backend.dto.VaccinationDTO;

import java.util.List;

public interface VaccinationService {
    void saveVaccination(VaccinationDTO dto);
    void updateVaccination(VaccinationDTO dto);
    List<VaccinationDTO> getAllVaccinations();
    List<VaccinationDTO> getVaccinationsByDog(int dogId);
    List<VaccinationDTO> getExpiringVaccinations();
    void deleteVaccination(int id);
}