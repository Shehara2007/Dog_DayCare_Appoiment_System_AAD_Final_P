package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.Repository.DogRepository;
import org.example.backend.Repository.VaccinationRepository;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.Vaccination;
import org.example.backend.dto.AppointmentDTOs;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final DogRepository dogRepository;

    @Transactional
    public AppointmentDTOs.VaccinationResponse addVaccination(AppointmentDTOs.VaccinationRequest request) {
        Dog dog = dogRepository.findById(request.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found"));

        Vaccination vaccination = Vaccination.builder()
                .dog(dog)
                .vaccineName(request.getVaccineName())
                .vaccinationDate(request.getVaccinationDate())
                .expiryDate(request.getExpiryDate())
                .administeredBy(request.getAdministeredBy())
                .batchNumber(request.getBatchNumber())
                .build();

        return mapToResponse(vaccinationRepository.save(vaccination));
    }

    public List<AppointmentDTOs.VaccinationResponse> getDogVaccinations(Long dogId) {
        return vaccinationRepository.findByDogId(dogId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AppointmentDTOs.VaccinationResponse> getAllVaccinations() {
        return vaccinationRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private AppointmentDTOs.VaccinationResponse mapToResponse(Vaccination v) {
        AppointmentDTOs.VaccinationResponse res = new AppointmentDTOs.VaccinationResponse();
        res.setId(v.getId());
        res.setDogId(v.getDog().getId());
        res.setDogName(v.getDog().getName());
        res.setVaccineName(v.getVaccineName());
        res.setVaccinationDate(v.getVaccinationDate());
        res.setExpiryDate(v.getExpiryDate());
        res.setAdministeredBy(v.getAdministeredBy());
        res.setBatchNumber(v.getBatchNumber());
        res.setAlertSent(v.isAlertSent());
        res.setCreatedAt(v.getCreatedAt());
        return res;
    }
}