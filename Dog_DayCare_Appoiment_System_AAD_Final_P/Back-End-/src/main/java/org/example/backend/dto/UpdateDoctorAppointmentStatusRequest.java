package org.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.EnumPackage.DoctorAppointmentStatus;

@Getter
@Setter
public class UpdateDoctorAppointmentStatusRequest {

    @NotNull
    private DoctorAppointmentStatus status;
}

