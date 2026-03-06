package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class doctorAppointmentsDTO {

    private int appointmentID;
    private String doctorName;
    private int dogID;
    private String appointmentDate;
    private String appointmentTime;
    private String reason;

}
