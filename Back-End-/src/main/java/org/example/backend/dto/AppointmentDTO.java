package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.EnumPackage.AppointmentStatus;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AppointmentDTO {
    private int appointmentId;
    private LocalDate appointmentDate;
    private String timeSlot;
    private String notes;
    private AppointmentStatus status;
    private int dogId;
    private int ownerId;
    private int serviceId;
    private int caretakerId;
}