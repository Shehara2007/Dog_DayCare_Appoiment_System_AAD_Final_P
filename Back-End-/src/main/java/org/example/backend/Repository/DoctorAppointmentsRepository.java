package org.example.backend.Repository;

import org.example.backend.Entity.doctorAppointmentsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorAppointmentsRepository extends JpaRepository<doctorAppointmentsEntity, Integer> {
}