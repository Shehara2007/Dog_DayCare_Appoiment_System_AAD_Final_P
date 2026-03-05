package org.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.EnumPackage.HealthStatus;

import java.time.LocalDate;

@Entity
@Table(name = "dog_reports")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HealthReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reportId;

    private LocalDate reportDate;
    private String eatingBehaviour;
    private String notes;

    @Enumerated(EnumType.STRING)
    private BehaviourType behaviourType;

    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caretaker_id", nullable = false)
    private Caretaker caretaker;
}