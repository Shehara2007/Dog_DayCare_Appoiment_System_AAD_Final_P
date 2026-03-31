package org.example.backend.Repository;

import org.example.backend.Entity.DoctorAppointment;
import org.example.backend.EnumPackage.DoctorAppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface DoctorAppointmentRepository extends JpaRepository<DoctorAppointment, Long> {

    boolean existsByDoctorIdAndAppointmentTimeAndStatusIn(Long doctorId,
                                                          LocalDateTime appointmentTime,
                                                          Collection<DoctorAppointmentStatus> statuses);

    List<DoctorAppointment> findByDogIdOrderByAppointmentTimeDesc(Long dogId);

    List<DoctorAppointment> findByOwnerIdOrderByAppointmentTimeDesc(Long ownerId);

    List<DoctorAppointment> findByDoctorIdOrderByAppointmentTimeDesc(Long doctorId);
}

