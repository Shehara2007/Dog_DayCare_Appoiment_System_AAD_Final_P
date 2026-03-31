package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.HealthReport;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.HealthStatus;
import org.example.backend.EnumPackage.NotificationType;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.Repository.HealthReportRepository;
import org.example.backend.dto.CreateHealthReportRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.service.DogService;
import org.example.backend.service.HealthReportService;
import org.example.backend.service.NotificationService;
import org.example.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HealthReportServiceImpl implements HealthReportService {

    private final HealthReportRepository healthReportRepository;
    private final DogService dogService;
    private final UserService userService;
    private final NotificationService notificationService;

    @Override
    public HealthReport create(CreateHealthReportRequest request) {
        Dog dog = dogService.getById(request.getDogId());
        User createdBy = userService.getById(request.getCreatedById());

        if (createdBy.getRole() != UserRole.ADMIN && createdBy.getRole() != UserRole.CARETAKER) {
            throw new BusinessException("Only ADMIN or CARETAKER can create health reports");
        }

        HealthReport report = new HealthReport();
        report.setDog(dog);
        report.setCreatedBy(createdBy);
        report.setBehaviour(request.getBehaviour());
        report.setHealthStatus(request.getHealthStatus());
        report.setNotes(request.getNotes());

        HealthReport saved = healthReportRepository.save(report);

        if (saved.getHealthStatus() == HealthStatus.BAD) {
            String message = "Health alert: " + dog.getName() + " has been marked as BAD health status.";
            notificationService.create(dog.getOwner(), dog, NotificationType.HEALTH_ALERT, message);
        }
        return saved;
    }

    @Override
    public List<HealthReport> getByDog(Long dogId) {
        return healthReportRepository.findByDogIdOrderByCreatedAtDesc(dogId);
    }

    @Override
    public List<HealthReport> getByQrToken(String qrToken) {
        Dog dog = dogService.getByQrAccessToken(qrToken);
        return healthReportRepository.findByDogIdOrderByCreatedAtDesc(dog.getId());
    }
}

