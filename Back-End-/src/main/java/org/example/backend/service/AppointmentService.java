package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.Repository.AppointmentRepository;
import org.example.backend.Repository.DogRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.Entity.Appointment;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.User;
import org.example.backend.dto.AppointmentDTOs;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    private static final int MAX_DOGS_PER_CARETAKER = 3;

    @Transactional
    public AppointmentDTOs.AppointmentResponse bookAppointment(AppointmentDTOs.AppointmentRequest request) {
        Dog dog = dogRepository.findById(request.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found"));

        User caretaker = null;
        if (request.getCaretakerId() != null) {
            caretaker = userRepository.findById(request.getCaretakerId())
                    .orElseThrow(() -> new RuntimeException("Caretaker not found"));

            // Check caretaker capacity
            long currentCount = appointmentRepository.countActiveAppointmentsByCaretakerAndDate(
                    caretaker.getId(), request.getAppointmentDate());
            if (currentCount >= MAX_DOGS_PER_CARETAKER) {
                throw new RuntimeException("Caretaker is fully booked on " + request.getAppointmentDate() +
                        " (max " + MAX_DOGS_PER_CARETAKER + " dogs)");
            }
        }

        Appointment appointment = Appointment.builder()
                .dog(dog)
                .caretaker(caretaker)
                .appointmentDate(request.getAppointmentDate())
                .timeSlot(request.getTimeSlot())
                .notes(request.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();

        appointment = appointmentRepository.save(appointment);
        return mapToResponse(appointment);
    }

    public List<AppointmentDTOs.AppointmentResponse> getMyAppointments() {
        User currentUser = getCurrentUser();
        return appointmentRepository.findByDogOwnerId(currentUser.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AppointmentDTOs.AppointmentResponse> getAllAppointments() {
        return appointmentRepository.findAll().stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AppointmentDTOs.AppointmentResponse> getCaretakerAppointments() {
        User currentUser = getCurrentUser();
        return appointmentRepository.findByCaretakerId(currentUser.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDTOs.AppointmentResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found"));

        if (status == AppointmentStatus.APPROVED && appointment.getCaretaker() != null) {
            long count = appointmentRepository.countActiveAppointmentsByCaretakerAndDate(
                    appointment.getCaretaker().getId(), appointment.getAppointmentDate());
            if (count >= MAX_DOGS_PER_CARETAKER) {
                throw new RuntimeException("Cannot approve: caretaker has reached maximum capacity");
            }
        }

        appointment.setStatus(status);
        appointmentRepository.save(appointment);

        // Notify owner
        User owner = appointment.getDog().getOwner();
        emailService.sendAppointmentConfirmation(
                owner.getEmail(), owner.getFullName(),
                appointment.getDog().getName(),
                appointment.getAppointmentDate().toString(),
                status.name()
        );

        return mapToResponse(appointment);
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Current user not found"));
    }

    private AppointmentDTOs.AppointmentResponse mapToResponse(Appointment a) {
        AppointmentDTOs.AppointmentResponse res = new AppointmentDTOs.AppointmentResponse();
        res.setId(a.getId());
        res.setDogId(a.getDog().getId());
        res.setDogName(a.getDog().getName());
        res.setAppointmentDate(a.getAppointmentDate());
        res.setTimeSlot(a.getTimeSlot());
        res.setNotes(a.getNotes());
        res.setStatus(a.getStatus());
        res.setCreatedAt(a.getCreatedAt());
        if (a.getCaretaker() != null) {
            res.setCaretakerId(a.getCaretaker().getId());
            res.setCaretakerName(a.getCaretaker().getFullName());
        }
        return res;
    }
}