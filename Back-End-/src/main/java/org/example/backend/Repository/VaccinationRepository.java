package org.example.backend.Repository;

import org.example.backend.Entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface VaccinationRepository extends JpaRepository<Vaccination, Long> {
    List<Vaccination> findByDogId(Long dogId);

    @Query("SELECT v FROM Vaccination v WHERE v.expiryDate BETWEEN :today AND :alertDate AND v.alertSent = false")
    List<Vaccination> findExpiringVaccinations(LocalDate today, LocalDate alertDate);
}