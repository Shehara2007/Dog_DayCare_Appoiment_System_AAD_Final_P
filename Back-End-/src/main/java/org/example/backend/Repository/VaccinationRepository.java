package org.example.backend.Repository;

import org.example.backend.Entity.Vaccination;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VaccinationRepository extends JpaRepository<Vaccination, Integer> {
    List<Vaccination> findByDog_DogId(int dogId);
    List<Vaccination> findByExpiryDateBefore(LocalDate date);
}