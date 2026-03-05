package org.example.backend.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Dog;
import org.example.backend.Repository.CaretakerRepository;
import org.example.backend.Repository.DogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class DogReportServiceImpl implements DogReportService {

    private final DogReportRepository dogReportRepository;
    private final DogRepository dogRepository;
    private final CaretakerRepository caretakerRepository;
    private final ModelMapper modelMapper;

    @Override
    public void saveReport(DogReportDTO dto) {
        Dog dog = dogRepository.findById(dto.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found with ID: " + dto.getDogId()));

        Caretaker caretaker = caretakerRepository.findById(dto.getCaretakerId())
                .orElseThrow(() -> new RuntimeException("Caretaker not found with ID: " + dto.getCaretakerId()));

        DogReport report = new DogReport();
        report.setReportDate(dto.getReportDate());
        report.setBehaviourType(dto.getBehaviourType());
        report.setHealthStatus(dto.getHealthStatus());
        report.setEatingBehaviour(dto.getEatingBehaviour());
        report.setNotes(dto.getNotes());
        report.setDog(dog);
        report.setCaretaker(caretaker);
        dogReportRepository.save(report);

        // Auto Risk Level Analysis
        if (dto.getBehaviourType() == BehaviourType.DANGEROUS) {
            long dangerousCount = dogReportRepository
                    .countByDog_DogIdAndBehaviourType(dto.getDogId(), BehaviourType.DANGEROUS);

            if (dangerousCount >= 3) {
                dog.setRiskLevel(RiskLevel.HIGH);
            } else if (dangerousCount == 2) {
                dog.setRiskLevel(RiskLevel.MEDIUM);
            }
            dogRepository.save(dog);
        }
    }

    @Override
    public void updateReport(DogReportDTO dto) {
        if (!dogReportRepository.existsById(dto.getReportId())) {
            throw new RuntimeException("Report not found with ID: " + dto.getReportId());
        }
        DogReport existing = dogReportRepository.findById(dto.getReportId()).get();
        existing.setReportDate(dto.getReportDate());
        existing.setBehaviourType(dto.getBehaviourType());
        existing.setHealthStatus(dto.getHealthStatus());
        existing.setEatingBehaviour(dto.getEatingBehaviour());
        existing.setNotes(dto.getNotes());
        dogReportRepository.save(existing);
    }

    @Override
    public List<DogReportDTO> getAllReports() {
        List<DogReport> reports = dogReportRepository.findAll();
        return modelMapper.map(reports, new TypeToken<List<DogReportDTO>>() {}.getType());
    }

    @Override
    public List<DogReportDTO> getReportsByDog(int dogId) {
        List<DogReport> reports = dogReportRepository.findByDog_DogId(dogId);
        return modelMapper.map(reports, new TypeToken<List<DogReportDTO>>() {}.getType());
    }

    @Override
    public DogReportDTO getReportById(int id) {
        DogReport report = dogReportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found with ID: " + id));
        return modelMapper.map(report, DogReportDTO.class);
    }

    @Override
    public void deleteReport(int id) {
        if (!dogReportRepository.existsById(id)) {
            throw new RuntimeException("Report not found with ID: " + id);
        }
        dogReportRepository.deleteById(id);
    }
}