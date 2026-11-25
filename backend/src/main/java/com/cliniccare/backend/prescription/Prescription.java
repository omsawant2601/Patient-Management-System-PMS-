package com.cliniccare.backend.prescription;

import com.cliniccare.backend.consultation.MedicalRecord;
import com.cliniccare.backend.patient.Patient;
import com.cliniccare.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.*;

@Entity @Table(name="prescriptions")
public class Prescription {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(name="prescription_number",nullable=false,unique=true) private String prescriptionNumber;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id",nullable=false) private Patient patient;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="doctor_id",nullable=false) private AppUser doctor;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="medical_record_id",nullable=false) private MedicalRecord medicalRecord;
    @Column(name="prescription_date",nullable=false) private Instant prescriptionDate=Instant.now();
    private String instructions;
    @Column(nullable=false) private String status="DRAFT";
    @Column(name="finalized_at") private Instant finalizedAt;
    @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
    @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
    @OneToMany(mappedBy="prescription",cascade=CascadeType.ALL,orphanRemoval=true)
    private List<PrescriptionItem> items=new ArrayList<>();
    public UUID getId(){return id;} public void setId(UUID v){id=v;}
    public String getPrescriptionNumber(){return prescriptionNumber;} public void setPrescriptionNumber(String v){prescriptionNumber=v;}
    public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;}
    public AppUser getDoctor(){return doctor;} public void setDoctor(AppUser v){doctor=v;}
    public MedicalRecord getMedicalRecord(){return medicalRecord;} public void setMedicalRecord(MedicalRecord v){medicalRecord=v;}
    public Instant getPrescriptionDate(){return prescriptionDate;} public void setPrescriptionDate(Instant v){prescriptionDate=v;}
    public String getInstructions(){return instructions;} public void setInstructions(String v){instructions=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public Instant getFinalizedAt(){return finalizedAt;} public void setFinalizedAt(Instant v){finalizedAt=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
    public List<PrescriptionItem> getItems(){return items;} public void setItems(List<PrescriptionItem> v){items=v;}
}
