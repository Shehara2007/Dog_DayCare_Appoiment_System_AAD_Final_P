package org.example.backend.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.Repository.DoctorAppointmentRepository;
import org.example.backend.Repository.DogRepository;
import org.example.backend.Repository.HealthReportRepository;
import org.example.backend.Repository.UserRepository;
import org.example.backend.Entity.DoctorAppointment;
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
public class DoctorAppointmentService {

    private final DoctorAppointmentRepository doctorAppointmentRepository;
    private final DogRepository dogRepository;
    private final UserRepository userRepository;
    private final HealthReportRepository healthReportRepository;
    private final EmailService emailService;

    @Transactional
    public AppointmentDTOs.DoctorAppointmentResponse bookAppointment(AppointmentDTOs.DoctorAppointmentRequest request) {
        Dog dog = dogRepository.findById(request.getDogId())
                .orElseThrow(() -> new RuntimeException("Dog not found"));
        User doctor = userRepository.findById(request.getDoctorId())
                .orElseThrow(() -> new RuntimeException("Doctor not found"));

        // Check doctor availability
        long count = doctorAppointmentRepository.countByDoctorAndDate(
                doctor.getId(), request.getAppointmentDate());
        if (count >= 10) {
            throw new RuntimeException("Doctor is fully booked on " + request.getAppointmentDate());
        }

        HealthReport healthReport = null;
        if (request.getHealthReportId() != null) {
            healthReport = healthReportRepository.findById(request.getHealthReportId()).orElse(null);
        }

        DoctorAppointment appointment = DoctorAppointment.builder()
                .dog(dog)
                .doctor(doctor)
                .healthReport(healthReport)
                .appointmentDate(request.getAppointmentDate())
                .appointmentTime(request.getAppointmentTime())
                .reason(request.getReason())
                .status(AppointmentStatus.PENDING)
                .build();

        return mapToResponse(doctorAppointmentRepository.save(appointment));
    }

    public List<AppointmentDTOs.DoctorAppointmentResponse> getMyDoctorAppointments() {
        User currentUser = getCurrentUser();
        return doctorAppointmentRepository.findByDogOwnerId(currentUser.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    public List<AppointmentDTOs.DoctorAppointmentResponse> getDoctorSchedule() {
        User currentUser = getCurrentUser();
        return doctorAppointmentRepository.findByDoctorId(currentUser.getId())
                .stream().map(this::mapToResponse).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentDTOs.DoctorAppointmentResponse updateStatus(Long id, AppointmentStatus status, String prescription, String diagnosis) {
        DoctorAppointment appt = doctorAppointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Doctor appointment not found"));
        appt.setStatus(status);
        if (prescription != null) appt.setPrescription(prescription);
        if (diagnosis != null) appt.setDiagnosis(diagnosis);
        return mapToResponse(doctorAppointmentRepository.save(appt));
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private AppointmentDTOs.DoctorAppointmentResponse mapToResponse(DoctorAppointment a) {
        AppointmentDTOs.DoctorAppointmentResponse res = new AppointmentDTOs.DoctorAppointmentResponse();
        res.setId(a.getId());
        res.setDogId(a.getDog().getId());
        res.setDogName(a.getDog().getName());
        res.setDoctorId(a.getDoctor().getId());
        res.setDoctorName(a.getDoctor().getFullName());
        res.setAppointmentDate(a.getAppointmentDate());
        res.setAppointmentTime(a.getAppointmentTime());
        res.setReason(a.getReason());
        res.setPrescription(a.getPrescription());
        res.setDiagnosis(a.getDiagnosis());
        res.setStatus(a.getStatus());
        res.setCreatedAt(a.getCreatedAt());
        if (a.getHealthReport() != null) res.setHealthReportId(a.getHealthReport().getId());
        return res;
    }
}