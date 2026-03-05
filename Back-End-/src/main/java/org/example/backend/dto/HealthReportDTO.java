package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.backend.EnumPackage.HealthStatus;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HealthReportDTO {
    private int reportId;
    private LocalDate reportDate;
    private String eatingBehaviour;
    private String notes;
    private BehaviourType behaviourType;
    private HealthStatus healthStatus;
    private int dogId;
    private int caretakerId;
}