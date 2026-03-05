package org.example.backend.sheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.Repository.HealthReportRepository;
import org.example.backend.Repository.VaccinationRepository;
import org.example.backend.Entity.Vaccination;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VaccinationAlertScheduler {

    private final VaccinationRepository vaccinationRepository;
    private final HealthReportRepository healthReportRepository;
    private final EmailService emailService;

    /**
     * Runs every day at 8:00 AM to check expiring vaccinations
     */
    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void checkVaccinationExpiry() {
        log.info("Running vaccination expiry check...");

        LocalDate today = LocalDate.now();
        LocalDate alertDate = today.plusDays(30); // Alert 30 days before expiry

        List<Vaccination> expiringVaccinations = vaccinationRepository
                .findExpiringVaccinations(today, alertDate);

        for (Vaccination vaccination : expiringVaccinations) {
            try {
                String ownerEmail = vaccination.getDog().getOwner().getEmail();
                String ownerName = vaccination.getDog().getOwner().getFullName();
                String dogName = vaccination.getDog().getName();
                String vaccineName = vaccination.getVaccineName();
                String expiryDate = vaccination.getExpiryDate().toString();

                emailService.sendVaccinationAlert(ownerEmail, ownerName, dogName, vaccineName, expiryDate);

                vaccination.setAlertSent(true);
                vaccinationRepository.save(vaccination);

                log.info("Vaccination alert sent for dog: {} - vaccine: {}", dogName, vaccineName);
            } catch (Exception e) {
                log.error("Failed to send vaccination alert for vaccination id {}: {}", vaccination.getId(), e.getMessage());
            }
        }

        log.info("Vaccination expiry check completed. Processed {} vaccinations.", expiringVaccinations.size());
    }

    /**
     * Reset alertSent flag monthly for already-expired vaccines
     */
    @Scheduled(cron = "0 0 0 1 * *")
    @Transactional
    public void resetExpiredAlerts() {
        log.info("Resetting alert flags for expired vaccinations...");
        LocalDate today = LocalDate.now();
        List<Vaccination> allVaccinations = vaccinationRepository.findAll();
        allVaccinations.stream()
                .filter(v -> v.getExpiryDate().isBefore(today) && v.isAlertSent())
                .forEach(v -> {
                    v.setAlertSent(false);
                    vaccinationRepository.save(v);
                });
    }
}