package org.example.backend.service;

import org.example.backend.Entity.DaycareAppointment;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.dto.BookDaycareAppointmentRequest;

import java.util.List;

public interface DaycareAppointmentService {
    DaycareAppointment book(BookDaycareAppointmentRequest request);
    DaycareAppointment updateStatus(Long appointmentId, AppointmentStatus status);
    List<DaycareAppointment> getByDog(Long dogId);
    List<DaycareAppointment> getByCaretaker(Long caretakerId);
}
