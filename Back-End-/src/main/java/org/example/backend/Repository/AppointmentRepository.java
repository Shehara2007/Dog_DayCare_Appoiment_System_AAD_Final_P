package org.example.backend.Repository;

import org.example.backend.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {
    List<Appointment> findByOwner_UserId(int ownerId);
    List<Appointment> findByDog_DogId(int dogId);
}