package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.EnumPackage.HealthStatus;
import org.example.backend.Repository.DogRepository;
import org.example.backend.Repository.HealthReportRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.HealthReport;
import org.example.backend.Entity.User;
import org.example.backend.dto.AppointmentDTOs;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HealthReportService {

    private final HealthReportRepository healthReportRepository;
    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Transactional
    public AppointmentDTOs.HealthReportResponse createReport(AppointmentDTOs.HealthReportRequest request) {
        Dog dog = dogRepository.findById(request.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found"));
        User currentUser = getCurrentUser();

        HealthReport report = HealthReport.builder()
                .dog(dog)
                .reportedBy(currentUser)
                .healthStatus(request.getHealthStatus())
                .behaviourStatus(request.getBehaviourStatus())
                .notes(request.getNotes())
                .temperature(request.getTemperature())
                .weight(request.getWeight())
                .build();

        report = healthReportRepository.save(report);

        // Send notification if health status is BAD
        if (request.getHealthStatus() == HealthStatus.BAD) {
            User owner = dog.getOwner();
            emailService.sendHealthAlert(
                    owner.getEmail(), owner.getFullName(), dog.getName(),
                    request.getHealthStatus().name(), request.getBehaviourStatus().name()
            );
            report.setOwnerNotified(true);
            healthReportRepository.save(report);
        }

        return mapToResponse(report);
    }

    public List<AppointmentDTOs.HealthReportResponse> getDogReports(Long dogId) {
        return healthReportRepository.findByDogIdOrderByReportedAtDesc(dogId)
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AppointmentDTOs.HealthReportResponse> getAllReports() {
        return healthReportRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AppointmentDTOs.HealthReportResponse mapToResponse(HealthReport r) {
        AppointmentDTOs.HealthReportResponse res = new AppointmentDTOs.HealthReportResponse();
        res.setId(r.getId());
        res.setDogId(r.getDog().getId());
        res.setDogName(r.getDog().getName());
        res.setHealthStatus(r.getHealthStatus());
        res.setBehaviourStatus(r.getBehaviourStatus());
        res.setNotes(r.getNotes());
        res.setTemperature(r.getTemperature());
        res.setWeight(r.getWeight());
        res.setOwnerNotified(r.isOwnerNotified());
        res.setReportedAt(r.getReportedAt());
        if (r.getReportedBy() != null) {
            res.setReportedByName(r.getReportedBy().getFullName());
        }
        return res;
    }
}