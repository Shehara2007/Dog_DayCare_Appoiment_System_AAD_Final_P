package org.example.backend.Repository;

import org.example.backend.Entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    List<Vaccination> findByDogId(Long dogId);

    List<Vaccination> findByExpiryDateBetweenAndAlertSentFalse(LocalDate startDate, LocalDate endDate);
}

