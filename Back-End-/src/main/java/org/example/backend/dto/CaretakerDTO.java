package org.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CaretakerDTO {
    private int caretakerId;
    private String caretakerName;
    private String phone;
    private int maxCapacity;
    private int currentLoad;
    private String status;
}