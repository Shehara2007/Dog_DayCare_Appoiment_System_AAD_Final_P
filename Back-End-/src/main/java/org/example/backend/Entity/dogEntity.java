package org.example.backend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class dogEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int DogId;
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
