package com.cliniccare.backend.patient;

import com.cliniccare.backend.user.AppUser;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="patient_photo_comparisons")
public class PatientPhotoComparison {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id",nullable=false) private Patient patient;
    @Column(name="before_image_key",nullable=false) private String beforeImageKey;
    @Column(name="after_image_key",nullable=false) private String afterImageKey;
    @Column(length=500) private String caption;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private AppUser createdBy;
    @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();

    public UUID getId(){return id;} public void setId(UUID value){id=value;}
    public Patient getPatient(){return patient;} public void setPatient(Patient value){patient=value;}
    public String getBeforeImageKey(){return beforeImageKey;} public void setBeforeImageKey(String value){beforeImageKey=value;}
    public String getAfterImageKey(){return afterImageKey;} public void setAfterImageKey(String value){afterImageKey=value;}
    public String getCaption(){return caption;} public void setCaption(String value){caption=value;}
    public AppUser getCreatedBy(){return createdBy;} public void setCreatedBy(AppUser value){createdBy=value;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant value){createdAt=value;}
}
