package com.cliniccare.backend.consultation;

import com.cliniccare.backend.appointment.Appointment;
import com.cliniccare.backend.patient.Patient;
import com.cliniccare.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="medical_records")
public class MedicalRecord {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id",nullable=false) private Patient patient;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="doctor_id",nullable=false) private AppUser doctor;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="appointment_id") private Appointment appointment;
    @Column(name="visit_date",nullable=false) private Instant visitDate=Instant.now();
    private String symptoms; private String diagnosis;
    @Column(name="clinical_notes") private String clinicalNotes;
    @Column(name="follow_up_recommendations") private String followUpRecommendations;
    @Column(nullable=false) private String status="DRAFT";
    @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
    @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
    public UUID getId(){return id;} public void setId(UUID v){id=v;}
    public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;}
    public AppUser getDoctor(){return doctor;} public void setDoctor(AppUser v){doctor=v;}
    public Appointment getAppointment(){return appointment;} public void setAppointment(Appointment v){appointment=v;}
    public Instant getVisitDate(){return visitDate;} public void setVisitDate(Instant v){visitDate=v;}
    public String getSymptoms(){return symptoms;} public void setSymptoms(String v){symptoms=v;}
    public String getDiagnosis(){return diagnosis;} public void setDiagnosis(String v){diagnosis=v;}
    public String getClinicalNotes(){return clinicalNotes;} public void setClinicalNotes(String v){clinicalNotes=v;}
    public String getFollowUpRecommendations(){return followUpRecommendations;} public void setFollowUpRecommendations(String v){followUpRecommendations=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
