package org.example.backend.service.custom;

import java.util.List;

public interface HealthReportService {
    void saveReport(DogReportDTO dto);
    void updateReport(DogReportDTO dto);
    List<DogReportDTO> getAllReports();
    List<DogReportDTO> getReportsByDog(int dogId);
    DogReportDTO getReportById(int id);
    void deleteReport(int id);
}