package com.cliniccare.backend.appointment;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.*;

public interface AppointmentRepository extends JpaRepository<Appointment,UUID> {
    @Query(value="select * from appointments a where a.doctor_id=:doctorId " +
           "and a.status in ('SCHEDULED','CONFIRMED') " +
           "and a.scheduled_at < :end and (a.scheduled_at + (a.duration_minutes || ' minutes')::interval) > :start",
           nativeQuery=true)
    List<Appointment> findOverlapping(@Param("doctorId") UUID doctorId,@Param("start") Instant start,@Param("end") Instant end);
    Page<Appointment> findByScheduledAtBetween(Instant start,Instant end,Pageable pageable);
    Page<Appointment> findByPatientId(UUID patientId,Pageable pageable);
    List<Appointment> findByPatientIdOrderByScheduledAtDesc(UUID patientId);
    Page<Appointment> findByDoctorId(UUID doctorId,Pageable pageable);
}
