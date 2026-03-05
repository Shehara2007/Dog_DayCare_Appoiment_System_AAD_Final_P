package org.example.backend.Repository;

import org.example.backend.Entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DogRepository extends JpaRepository<Dog, Integer> {
    List<Dog> findByOwner_UserId(int ownerId);
    List<Dog> findByVaccinationExpiryDateBefore(LocalDate date);
}