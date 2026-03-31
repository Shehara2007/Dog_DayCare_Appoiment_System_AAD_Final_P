package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.User;
import org.example.backend.Entity.Vaccination;
import org.example.backend.EnumPackage.NotificationType;
import org.example.backend.Repository.VaccinationRepository;
import org.example.backend.dto.CreateVaccinationRequest;
import org.example.backend.service.DogService;
import org.example.backend.service.NotificationService;
import org.example.backend.service.VaccinationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VaccinationServiceImpl implements VaccinationService {

    private final VaccinationRepository vaccinationRepository;
    private final DogService dogService;
    private final NotificationService notificationService;

    @Override
    public Vaccination create(CreateVaccinationRequest request) {
        Dog dog = dogService.getById(request.getDogId());

        Vaccination vaccination = new Vaccination();
        vaccination.setDog(dog);
        vaccination.setVaccineName(request.getVaccineName());
        vaccination.setGivenDate(request.getGivenDate());
        vaccination.setExpiryDate(request.getExpiryDate());
        return vaccinationRepository.save(vaccination);
    }

    @Override
    public List<Vaccination> getByDog(Long dogId) {
        return vaccinationRepository.findByDogId(dogId);
    }

    @Override
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void processDailyVaccinationAlerts() {
        LocalDate today = LocalDate.now();
        LocalDate alertUntil = today.plusDays(7);

        List<Vaccination> expiringSoon = vaccinationRepository
                .findByExpiryDateBetweenAndAlertSentFalse(today, alertUntil);

        for (Vaccination vaccination : expiringSoon) {
            Dog dog = vaccination.getDog();
            User owner = dog.getOwner();

            String message = String.format(
                    "Vaccination '%s' for dog %s will expire on %s",
                    vaccination.getVaccineName(),
                    dog.getName(),
                    vaccination.getExpiryDate()
            );
            notificationService.create(owner, dog, NotificationType.VACCINATION_ALERT, message);
            vaccination.setAlertSent(true);
        }

        if (!expiringSoon.isEmpty()) {
            vaccinationRepository.saveAll(expiringSoon);
        }
        log.info("Vaccination alert scheduler processed {} records", expiringSoon.size());
    }
}

