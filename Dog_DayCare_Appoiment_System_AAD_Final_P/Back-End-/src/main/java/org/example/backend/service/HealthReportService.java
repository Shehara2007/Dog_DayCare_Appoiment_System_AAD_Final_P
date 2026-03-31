package org.example.backend.service;

import org.example.backend.Entity.HealthReport;
import org.example.backend.dto.CreateHealthReportRequest;

import java.util.List;

public interface HealthReportService {
    HealthReport create(CreateHealthReportRequest request);
    List<HealthReport> getByDog(Long dogId);
    List<HealthReport> getByQrToken(String qrToken);
}
