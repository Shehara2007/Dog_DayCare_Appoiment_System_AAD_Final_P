package org.example.backend.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HealthReportRepository extends JpaRepository<DogReport, Integer> {
    List<DogReport> findByDog_DogId(int dogId);
    long countByDog_DogIdAndBehaviourType(int dogId, BehaviourType behaviourType);
}