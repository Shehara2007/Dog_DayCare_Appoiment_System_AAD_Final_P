package org.example.backend.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity

public class healthReportsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int reportID;
    private int dogID;
    private Date reportDate;
    private String behaviourType;
    private String healthCondition;
    private String eatingBehaviour;
    private String notes;
    private String careTaker;
    private String notifyOwner;

}