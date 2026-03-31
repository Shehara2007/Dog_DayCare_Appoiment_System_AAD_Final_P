package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.DaycareAppointment;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.Repository.DaycareAppointmentRepository;
import org.example.backend.dto.BookDaycareAppointmentRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.exeception.ResourceNotFoundException;
import org.example.backend.service.DaycareAppointmentService;
import org.example.backend.service.DogService;
import org.example.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DaycareAppointmentServiceImpl implements DaycareAppointmentService {

    private static final int MAX_DOGS_PER_CARETAKER = 3;

    private final DaycareAppointmentRepository appointmentRepository;
    private final DogService dogService;
    private final UserService userService;

    @Override
    public DaycareAppointment book(BookDaycareAppointmentRequest request) {
        validateTimeRange(request.getStartTime(), request.getEndTime());

        Dog dog = dogService.getById(request.getDogId());
        User caretaker = userService.getById(request.getCaretakerId());
        if (caretaker.getRole() != UserRole.CARETAKER) {
            throw new BusinessException("Selected user is not a caretaker");
        }

        long activeCount = appointmentRepository.countOverlappingAppointments(
                caretaker.getId(),
                request.getStartTime(),
                request.getEndTime(),
                List.of(AppointmentStatus.PENDING, AppointmentStatus.APPROVED)
        );
        if (activeCount >= MAX_DOGS_PER_CARETAKER) {
            throw new BusinessException("Caretaker already has maximum 3 dogs for this time slot");
        }

        DaycareAppointment appointment = new DaycareAppointment();
        appointment.setDog(dog);
        appointment.setCaretaker(caretaker);
        appointment.setStartTime(request.getStartTime());
        appointment.setEndTime(request.getEndTime());
        appointment.setStatus(AppointmentStatus.PENDING);
        return appointmentRepository.save(appointment);
    }

    @Override
    public DaycareAppointment updateStatus(Long appointmentId, AppointmentStatus status) {
        DaycareAppointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found: " + appointmentId));
        appointment.setStatus(status);
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<DaycareAppointment> getByDog(Long dogId) {
        return appointmentRepository.findByDogIdOrderByStartTimeDesc(dogId);
    }

    @Override
    public List<DaycareAppointment> getByCaretaker(Long caretakerId) {
        return appointmentRepository.findByCaretakerIdOrderByStartTimeDesc(caretakerId);
    }

    private void validateTimeRange(LocalDateTime start, LocalDateTime end) {
        if (!end.isAfter(start)) {
            throw new BusinessException("End time must be after start time");
        }
    }
}

