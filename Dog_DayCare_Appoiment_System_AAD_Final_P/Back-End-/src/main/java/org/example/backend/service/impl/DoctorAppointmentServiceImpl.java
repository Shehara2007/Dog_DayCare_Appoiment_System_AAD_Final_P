package org.example.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.backend.Entity.DoctorAppointment;
import org.example.backend.Entity.Dog;
import org.example.backend.Entity.User;
import org.example.backend.EnumPackage.DoctorAppointmentStatus;
import org.example.backend.EnumPackage.UserRole;
import org.example.backend.Repository.DoctorAppointmentRepository;
import org.example.backend.dto.CreateDoctorAppointmentRequest;
import org.example.backend.exeception.BusinessException;
import org.example.backend.exeception.ResourceNotFoundException;
import org.example.backend.service.DoctorAppointmentService;
import org.example.backend.service.DogService;
import org.example.backend.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoctorAppointmentServiceImpl implements DoctorAppointmentService {

    private final DoctorAppointmentRepository repository;
    private final DogService dogService;
    private final UserService userService;

    @Override
    public DoctorAppointment create(CreateDoctorAppointmentRequest request) {
        Dog dog = dogService.getById(request.getDogId());
        User owner = userService.getById(request.getOwnerId());
        User doctor = userService.getById(request.getDoctorId());

        if (owner.getRole() != UserRole.PET_OWNER) {
            throw new BusinessException("Owner must have PET_OWNER role");
        }
        if (doctor.getRole() != UserRole.DOCTOR) {
            throw new BusinessException("Selected user is not a doctor");
        }

        boolean occupied = repository.existsByDoctorIdAndAppointmentTimeAndStatusIn(
                doctor.getId(),
                request.getAppointmentTime(),
                List.of(DoctorAppointmentStatus.REQUESTED, DoctorAppointmentStatus.CONFIRMED)
        );
        if (occupied) {
            throw new BusinessException("Doctor is not available for the selected time");
        }

        DoctorAppointment appointment = new DoctorAppointment();
        appointment.setDog(dog);
        appointment.setOwner(owner);
        appointment.setDoctor(doctor);
        appointment.setAppointmentTime(request.getAppointmentTime());
        appointment.setNotes(request.getNotes());
        appointment.setStatus(DoctorAppointmentStatus.REQUESTED);
        return repository.save(appointment);
    }

    @Override
    public List<DoctorAppointment> getByOwner(Long ownerId) {
        return repository.findByOwnerIdOrderByAppointmentTimeDesc(ownerId);
    }

    @Override
    public DoctorAppointment updateStatus(Long appointmentId, DoctorAppointmentStatus status) {
        DoctorAppointment appointment = repository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Doctor appointment not found: " + appointmentId));
        appointment.setStatus(status);
        return repository.save(appointment);
    }

    @Override
    public List<DoctorAppointment> getByDog(Long dogId) {
        return repository.findByDogIdOrderByAppointmentTimeDesc(dogId);
    }

    @Override
    public List<DoctorAppointment> getByDoctor(Long doctorId) {
        return repository.findByDoctorIdOrderByAppointmentTimeDesc(doctorId);
    }
}

