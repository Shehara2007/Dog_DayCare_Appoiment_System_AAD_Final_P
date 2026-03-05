package org.example.backend.Entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "caretakers")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Caretaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int caretakerId;

    private String caretakerName;
    private String phone;
    private int maxCapacity;
    private int currentLoad;
    private String status;

    @OneToMany(mappedBy = "caretaker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "caretaker", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DogReport> dogReports;
}