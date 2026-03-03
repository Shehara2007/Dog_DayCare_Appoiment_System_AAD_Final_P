package org.example.backend.Repository;

import org.example.backend.EnumPackage.HealthStatus;
import org.example.backend.Entity.HealthReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HealthReportRepository extends JpaRepository<HealthReport, Long> {
    List<HealthReport> findByDogId(Long dogId);
    List<HealthReport> findByDogIdOrderByReportedAtDesc(Long dogId);
    List<HealthReport> findByHealthStatusAndOwnerNotifiedFalse(HealthStatus status);
}