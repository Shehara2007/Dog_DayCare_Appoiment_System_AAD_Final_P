package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DoctorDTO {
    private int doctorId;
    private String doctorName;
    private String specialization;
    private String phone;
    private String email;
    private String availableDays;
}