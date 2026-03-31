package org.example.backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.EnumPackage.BehaviourType;
import org.example.backend.EnumPackage.HealthStatus;

@Getter
@Setter
public class CreateHealthReportRequest {

    @NotNull
    private Long dogId;

    @NotNull
    private Long createdById;

    @NotNull
    private BehaviourType behaviour;

    @NotNull
    private HealthStatus healthStatus;

    private String notes;
}

