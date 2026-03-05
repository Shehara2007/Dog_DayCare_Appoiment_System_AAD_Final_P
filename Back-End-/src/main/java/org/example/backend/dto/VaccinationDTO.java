package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class VaccinationDTO {
    private int vaccinationId;
    private String vaccineName;
    private LocalDate dateGiven;
    private LocalDate expiryDate;
    private int dogId;
}