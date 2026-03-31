package org.example.backend.Repository;

import org.example.backend.Entity.DaycareAppointment;
import org.example.backend.EnumPackage.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DaycareAppointmentRepository extends JpaRepository<DaycareAppointment, Long> {

    List<DaycareAppointment> findByDogIdOrderByStartTimeDesc(Long dogId);

    List<DaycareAppointment> findByCaretakerIdOrderByStartTimeDesc(Long caretakerId);

    @Query("""
            select count(a) from DaycareAppointment a
            where a.caretaker.id = :caretakerId
              and a.status in :activeStatuses
              and :startTime < a.endTime
              and :endTime > a.startTime
            """)
    long countOverlappingAppointments(@Param("caretakerId") Long caretakerId,
                                      @Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime,
                                      @Param("activeStatuses") List<AppointmentStatus> activeStatuses);
}


