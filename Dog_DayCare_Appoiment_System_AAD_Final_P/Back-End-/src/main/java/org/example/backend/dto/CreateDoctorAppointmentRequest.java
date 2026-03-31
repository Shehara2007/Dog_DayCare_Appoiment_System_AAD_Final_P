package org.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CreateDoctorAppointmentRequest {

    @NotNull
    private Long dogId;

    @NotNull
    private Long ownerId;

    @NotNull
    private Long doctorId;

    @NotNull
    private LocalDateTime appointmentTime;

    private String notes;
}

