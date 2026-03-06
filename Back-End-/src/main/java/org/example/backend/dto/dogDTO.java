package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class dogDTO {

    private int dogID;
    private String dogName;
    private String dogBreed;
    private int age;
    private String gender;
    private int ownerID;
    private int vaccinationExpiry;
    private String behaviourType;
    private String healthStatus;
    private String specialNotes;
}
