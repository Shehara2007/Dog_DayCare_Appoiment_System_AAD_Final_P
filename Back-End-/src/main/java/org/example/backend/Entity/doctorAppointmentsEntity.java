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

public class doctorAppointmentsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private int appointmentID;
    private String doctorName;
    private int dogID;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;
}
