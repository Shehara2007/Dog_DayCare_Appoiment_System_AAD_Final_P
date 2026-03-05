package org.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "daycare_services")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DaycareService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int serviceId;

    private String serviceName;
    private double price;
    private int durationHours;
    private String description;

    @OneToMany(mappedBy = "daycareService", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;
}