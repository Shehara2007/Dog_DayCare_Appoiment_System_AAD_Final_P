package org.example.backend.service;

import org.example.backend.Entity.DoctorAppointment;
import org.example.backend.EnumPackage.DoctorAppointmentStatus;
import org.example.backend.dto.CreateDoctorAppointmentRequest;

import java.util.List;

public interface DoctorAppointmentService {
    DoctorAppointment create(CreateDoctorAppointmentRequest request);
    DoctorAppointment updateStatus(Long appointmentId, DoctorAppointmentStatus status);
    List<DoctorAppointment> getByDog(Long dogId);
    List<DoctorAppointment> getByOwner(Long ownerId);
    List<DoctorAppointment> getByDoctor(Long doctorId);
}
