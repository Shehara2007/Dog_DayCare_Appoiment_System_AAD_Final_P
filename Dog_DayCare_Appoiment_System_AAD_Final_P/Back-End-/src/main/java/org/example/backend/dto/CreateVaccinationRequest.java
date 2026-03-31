package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateVaccinationRequest {

    @NotNull
    private Long dogId;

    @NotBlank
    private String vaccineName;

    @NotNull
    private LocalDate givenDate;

    @NotNull
    private LocalDate expiryDate;
}

