package org.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.EnumPackage.AppointmentStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "doctor_appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "health_report_id")
    private HealthReport healthReport;

    @Column(nullable = false)
    private LocalDate appointmentDate;

    private LocalTime appointmentTime;

    private String reason;

    private String prescription;

    private String diagnosis;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}