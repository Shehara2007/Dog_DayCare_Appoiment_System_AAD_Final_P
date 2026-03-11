package org.example.backend.Repository;

import org.example.backend.Entity.healthReportsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HealthReportsRepository extends JpaRepository<healthReportsEntity, Integer> {

}