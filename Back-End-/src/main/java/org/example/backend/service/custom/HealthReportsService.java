package org.example.backend.service.custom;

import org.example.backend.dto.healthReportsDTO;

import java.util.List;

public interface HealthReportsService {

    void saveReport(healthReportsDTO reportDTO);

    void updateReport(healthReportsDTO reportDTO);

    void deleteReport(healthReportsDTO reportDTO);

    List<healthReportsDTO> getAllReports();

}