package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class DaycareServiceDTO {
    private int serviceId;
    private String serviceName;
    private double price;
    private int durationHours;
    private String description;
}