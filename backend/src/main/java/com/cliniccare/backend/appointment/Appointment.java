package com.cliniccare.backend.appointment;

import com.cliniccare.backend.patient.Patient;
import com.cliniccare.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="appointments")
public class Appointment {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(name="appointment_number",nullable=false,unique=true) private String appointmentNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id",nullable=false) private Patient patient;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="doctor_id") private AppUser doctor;
    @Column(name="scheduled_at",nullable=false) private Instant scheduledAt;
    @Column(name="duration_minutes",nullable=false) private int durationMinutes=15;
    @Column(name="appointment_type") private String appointmentType="CONSULTATION";
    @Column(nullable=false) private String status="SCHEDULED";
    private String notes;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private AppUser createdBy;
    @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
    @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
    @Version private Long version;
    public UUID getId(){return id;} public void setId(UUID v){id=v;}
    public String getAppointmentNumber(){return appointmentNumber;} public void setAppointmentNumber(String v){appointmentNumber=v;}
    public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;}
    public AppUser getDoctor(){return doctor;} public void setDoctor(AppUser v){doctor=v;}
    public Instant getScheduledAt(){return scheduledAt;} public void setScheduledAt(Instant v){scheduledAt=v;}
    public int getDurationMinutes(){return durationMinutes;} public void setDurationMinutes(int v){durationMinutes=v;}
    public String getAppointmentType(){return appointmentType;} public void setAppointmentType(String v){appointmentType=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getNotes(){return notes;} public void setNotes(String v){notes=v;}
    public AppUser getCreatedBy(){return createdBy;} public void setCreatedBy(AppUser v){createdBy=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
