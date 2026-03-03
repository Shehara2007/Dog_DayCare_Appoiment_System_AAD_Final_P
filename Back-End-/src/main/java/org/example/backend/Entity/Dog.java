package org.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.backend.EnumPackage.HealthStatus;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "dogs")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dogId;

    private String dogName;
    private String breed;
    private int age;
    private String gender;
    private LocalDate vaccinationExpiryDate;
    private String specialNotes;

    @Enumerated(EnumType.STRING)
    private BehaviourType behaviourType = BehaviourType.FRIENDLY;

    @Enumerated(EnumType.STRING)
    private HealthStatus healthStatus = HealthStatus.HEALTHY;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel = RiskLevel.LOW;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Vaccination> vaccinations;

    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DogReport> reports;

    @OneToMany(mappedBy = "dog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}