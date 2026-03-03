package org.example.backend.Repository;

import org.example.backend.EnumPackage.AppointmentStatus;
import org.example.backend.Entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDogId(Long dogId);
    List<Appointment> findByCaretakerId(Long caretakerId);
    List<Appointment> findByDogOwnerId(Long ownerId);
    List<Appointment> findByStatus(AppointmentStatus status);

    @Query("SELECT COUNT(a) FROM Appointment a WHERE a.caretaker.id = :caretakerId " +
           "AND a.appointmentDate = :date AND a.status = 'APPROVED'")
    long countActiveAppointmentsByCaretakerAndDate(Long caretakerId, LocalDate date);

    List<Appointment> findByCaretakerIdAndAppointmentDateAndStatus(
            Long caretakerId, LocalDate date, AppointmentStatus status);
}