package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.healthReportsEntity;
import org.example.backend.Repository.HealthReportsRepository;
import org.example.backend.dto.healthReportsDTO;
import org.example.backend.exeception.DogNotFoundException;
import org.example.backend.service.custom.HealthReportsService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Service
@Transactional

public class HealthReportsServiceImpl implements HealthReportsService {

    private final HealthReportsRepository healthReportsRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveReport(healthReportsDTO reportDTO) {

        reportDTO.setReportID(0);
        healthReportsEntity report = modelMapper.map(reportDTO, healthReportsEntity.class);
        healthReportsRepository.save(report);

    }

    @Override
    public void updateReport(healthReportsDTO reportDTO) {

        healthReportsEntity existingReport = healthReportsRepository
                .findById(reportDTO.getReportID())
                .orElseThrow(() -> new DogNotFoundException(
                        "Report not found with ID: " + reportDTO.getReportID()));

        existingReport.setDogID(reportDTO.getDogID());
        existingReport.setReportDate(reportDTO.getReportDate());
        existingReport.setBehaviourType(reportDTO.getBehaviourType());
        existingReport.setHealthCondition(reportDTO.getHealthCondition());
        existingReport.setEatingBehaviour(reportDTO.getEatingBehaviour());
        existingReport.setNotes(reportDTO.getNotes());
        existingReport.setCareTaker(reportDTO.getCareTaker());
        existingReport.setNotifyOwner(reportDTO.getNotifyOwner());

        healthReportsRepository.saveAndFlush(existingReport);

    }

    @Override
    public void deleteReport(healthReportsDTO reportDTO) {

        healthReportsEntity existingReport = healthReportsRepository
                .findById(reportDTO.getReportID())
                .orElseThrow(() -> new DogNotFoundException(
                        "Report not found with ID: " + reportDTO.getReportID()));

        healthReportsRepository.delete(existingReport);
        healthReportsRepository.flush();

    }

    @Override
    @Transactional(readOnly = true)
    public List<healthReportsDTO> getAllReports() {

        List<healthReportsEntity> list = healthReportsRepository.findAll();

        return modelMapper.map(list,
                new TypeToken<List<healthReportsDTO>>() {}.getType());

    }
}