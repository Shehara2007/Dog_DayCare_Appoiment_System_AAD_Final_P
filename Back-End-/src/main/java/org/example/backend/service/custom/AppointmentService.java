package org.example.backend.service.custom;

import org.example.backend.dto.AppointmentDTO;

import java.util.List;

public interface AppointmentService {
    void saveAppointment(AppointmentDTO dto);
    void updateAppointment(AppointmentDTO dto);
    List<AppointmentDTO> getAllAppointments();
    List<AppointmentDTO> getAppointmentsByOwner(int ownerId);
    AppointmentDTO getAppointmentById(int id);
    void approveAppointment(int id);
    void rejectAppointment(int id);
    void deleteAppointment(int id);
}