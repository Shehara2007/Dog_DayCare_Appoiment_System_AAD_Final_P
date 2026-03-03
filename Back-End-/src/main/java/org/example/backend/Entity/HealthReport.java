package org.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.EnumPackage.BehaviourStatus;
import org.example.backend.EnumPackage.HealthStatus;

import java.time.LocalDateTime;

@Entity
@Table(name = "health_reports")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealthReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dog_id", nullable = false)
    private Dog dog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reported_by")
    private User reportedBy;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HealthStatus healthStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BehaviourStatus behaviourStatus;

    @Column(length = 1000)
    private String notes;

    private String temperature;

    private String weight;

    @Builder.Default
    private boolean ownerNotified = false;

    @Column(updatable = false)
    @Builder.Default
    private LocalDateTime reportedAt = LocalDateTime.now();
}