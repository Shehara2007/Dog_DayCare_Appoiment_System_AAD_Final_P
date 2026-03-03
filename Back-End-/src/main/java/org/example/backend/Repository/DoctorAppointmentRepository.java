package org.example.backend.Repository;

import org.example.backend.Entity.DoctorAppointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface DoctorAppointmentRepository extends JpaRepository<DoctorAppointment, Long> {
    List<DoctorAppointment> findByDogId(Long dogId);
    List<DoctorAppointment> findByDoctorId(Long doctorId);
    List<DoctorAppointment> findByDogOwnerId(Long ownerId);

    @Query("SELECT COUNT(da) FROM DoctorAppointment da WHERE da.doctor.id = :doctorId " +
           "AND da.appointmentDate = :date AND da.status = 'APPROVED'")
    long countByDoctorAndDate(Long doctorId, LocalDate date);
}