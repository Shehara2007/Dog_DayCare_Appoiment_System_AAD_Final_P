package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.EnumPackage.HealthStatus;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DogDTO {
    private int dogId;
    private String dogName;
    private String breed;
    private int age;
    private String gender;
    private LocalDate vaccinationExpiryDate;
    private String specialNotes;
    private BehaviourType behaviourType;
    private HealthStatus healthStatus;
    private RiskLevel riskLevel;
    private int ownerId;
}