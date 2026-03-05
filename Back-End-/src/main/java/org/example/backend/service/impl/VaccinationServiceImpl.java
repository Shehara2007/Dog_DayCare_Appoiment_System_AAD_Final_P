package org.example.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.Vaccination;
import org.example.backend.Repository.DogRepository;
import org.example.backend.Repository.VaccinationRepository;
import org.example.backend.dto.VaccinationDTO;
import org.example.backend.service.custom.VaccinationService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class VaccinationServiceImpl implements VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final DogRepository dogRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveVaccination(VaccinationDTO dto) {
        Dog dog = dogRepository.findById(dto.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found with ID: " + dto.getDogId()));
        Vaccination vaccination = modelMapper.map(dto, Vaccination.class);
        vaccination.setDog(dog);

        // Update dog's vaccination expiry date
        dog.setVaccinationExpiryDate(dto.getExpiryDate());
        dogRepository.save(dog);

        vaccinationRepository.save(vaccination);
    }

    @Override
    public void updateVaccination(VaccinationDTO dto) {
        if (!vaccinationRepository.existsById(dto.getVaccinationId())) {
            throw new RuntimeException("Vaccination not found with ID: " + dto.getVaccinationId());
        }
        Dog dog = dogRepository.findById(dto.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found with ID: " + dto.getDogId()));
        Vaccination vaccination = modelMapper.map(dto, Vaccination.class);
        vaccination.setDog(dog);
        vaccinationRepository.save(vaccination);
    }

    @Override
    public List<VaccinationDTO> getAllVaccinations() {
        List<Vaccination> vaccinations = vaccinationRepository.findAll();
        return modelMapper.map(vaccinations, new TypeToken<List<VaccinationDTO>>() {}.getType());
    }

    @Override
    public List<VaccinationDTO> getVaccinationsByDog(int dogId) {
        List<Vaccination> vaccinations = vaccinationRepository.findByDog_DogId(dogId);
        return modelMapper.map(vaccinations, new TypeToken<List<VaccinationDTO>>() {}.getType());
    }

    @Override
    public List<VaccinationDTO> getExpiringVaccinations() {
        // Returns vaccinations expiring within next 7 days
        LocalDate threshold = LocalDate.now().plusDays(7);
        List<Vaccination> vaccinations = vaccinationRepository.findByExpiryDateBefore(threshold);
        return modelMapper.map(vaccinations, new TypeToken<List<VaccinationDTO>>() {}.getType());
    }

    @Override
    public void deleteVaccination(int id) {
        if (!vaccinationRepository.existsById(id)) {
            throw new RuntimeException("Vaccination not found with ID: " + id);
        }
        vaccinationRepository.deleteById(id);
    }
}