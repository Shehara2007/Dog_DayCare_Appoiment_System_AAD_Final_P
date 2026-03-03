package org.example.backend.dto;

import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.EnumPackage.BehaviourStatus;
import org.example.backend.EnumPackage.HealthStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

// ===== APPOINTMENT DTOs =====
public class AppointmentDTOs {

    @Data
    public static class AppointmentRequest {
        @NotNull private Long dogId;
        private Long caretakerId;
        @NotNull
        private LocalDate appointmentDate;
        private String timeSlot;
        private String notes;
    }

    @Data
    public static class AppointmentResponse {
        private Long id;
        private Long dogId;
        private String dogName;
        private Long caretakerId;
        private String caretakerName;
        private LocalDate appointmentDate;
        private String timeSlot;
        private String notes;
        private AppointmentStatus status;
        private LocalDateTime createdAt;
    }

    // ===== VACCINATION DTOs =====
    @Data
    public static class VaccinationRequest {
        @NotNull private Long dogId;
        private String vaccineName;
        private LocalDate vaccinationDate;
        @NotNull private LocalDate expiryDate;
        private String administeredBy;
        private String batchNumber;
    }

    @Data
    public static class VaccinationResponse {
        private Long id;
        private Long dogId;
        private String dogName;
        private String vaccineName;
        private LocalDate vaccinationDate;
        private LocalDate expiryDate;
        private String administeredBy;
        private String batchNumber;
        private boolean alertSent;
        private LocalDateTime createdAt;
    }

    // ===== HEALTH REPORT DTOs =====
    @Data
    public static class HealthReportRequest {
        @NotNull private Long dogId;
        @NotNull private HealthStatus healthStatus;
        @NotNull private BehaviourStatus behaviourStatus;
        private String notes;
        private String temperature;
        private String weight;
    }

    @Data
    public static class HealthReportResponse {
        private Long id;
        private Long dogId;
        private String dogName;
        private String reportedByName;
        private HealthStatus healthStatus;
        private BehaviourStatus behaviourStatus;
        private String notes;
        private String temperature;
        private String weight;
        private boolean ownerNotified;
        private LocalDateTime reportedAt;
    }

    // ===== DOCTOR APPOINTMENT DTOs =====
    @Data
    public static class DoctorAppointmentRequest {
        @NotNull private Long dogId;
        @NotNull private Long doctorId;
        private Long healthReportId;
        @NotNull private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String reason;
    }

    @Data
    public static class DoctorAppointmentResponse {
        private Long id;
        private Long dogId;
        private String dogName;
        private Long doctorId;
        private String doctorName;
        private Long healthReportId;
        private LocalDate appointmentDate;
        private LocalTime appointmentTime;
        private String reason;
        private String prescription;
        private String diagnosis;
        private AppointmentStatus status;
        private LocalDateTime createdAt;
    }
}