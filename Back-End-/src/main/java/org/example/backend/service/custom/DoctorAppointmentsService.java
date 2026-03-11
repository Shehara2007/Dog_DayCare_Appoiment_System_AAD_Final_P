package org.example.backend.service.custom;

import org.example.backend.dto.doctorAppointmentsDTO;

import java.util.List;

public interface DoctorAppointmentsService {
    void saveAppointment(doctorAppointmentsDTO dto);
    void updateAppointment(doctorAppointmentsDTO dto);
    void deleteAppointment(doctorAppointmentsDTO dto);
    List<doctorAppointmentsDTO> getAllAppointments();
}