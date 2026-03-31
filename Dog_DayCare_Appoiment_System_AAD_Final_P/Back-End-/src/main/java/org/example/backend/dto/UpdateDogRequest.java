package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateDogRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String breed;

    private LocalDate dateOfBirth;
}

