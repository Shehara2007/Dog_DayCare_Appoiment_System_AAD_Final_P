package org.example.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateDogRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String breed;

    private LocalDate dateOfBirth;

    @NotNull
    private Long ownerId;
}

