package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class healthReportsDTO {

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
