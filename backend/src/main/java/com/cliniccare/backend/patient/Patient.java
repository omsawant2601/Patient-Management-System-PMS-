package com.cliniccare.backend.patient;

import com.cliniccare.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.*;
import java.util.UUID;

@Entity @Table(name="patients")
public class Patient {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @Column(name="patient_code", nullable=false, unique=true) private String patientCode;
    @Column(name="full_name", nullable=false) private String fullName;
    @Column(nullable=false) private String mobile;
    @Column(name="alternate_contact_number") private String alternateContactNumber;
    private String address;
    @Column(name="date_of_birth") private LocalDate dateOfBirth;
    private Integer age;
    private String gender;
    @Column(name="registration_date") private LocalDate registrationDate=LocalDate.now();
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="created_by") private AppUser createdBy;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="updated_by") private AppUser updatedBy;
    @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
    @Column(name="updated_at",nullable=false) private Instant updatedAt=Instant.now();
    public UUID getId(){return id;} public void setId(UUID v){id=v;}
    public String getPatientCode(){return patientCode;} public void setPatientCode(String v){patientCode=v;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getMobile(){return mobile;} public void setMobile(String v){mobile=v;}
    public String getAlternateContactNumber(){return alternateContactNumber;} public void setAlternateContactNumber(String v){alternateContactNumber=v;}
    public String getAddress(){return address;} public void setAddress(String v){address=v;}
    public LocalDate getDateOfBirth(){return dateOfBirth;} public void setDateOfBirth(LocalDate v){dateOfBirth=v;}
    public Integer getAge(){return age;} public void setAge(Integer v){age=v;}
    public String getGender(){return gender;} public void setGender(String v){gender=v;}
    public LocalDate getRegistrationDate(){return registrationDate;} public void setRegistrationDate(LocalDate v){registrationDate=v;}
    public AppUser getCreatedBy(){return createdBy;} public void setCreatedBy(AppUser v){createdBy=v;}
    public AppUser getUpdatedBy(){return updatedBy;} public void setUpdatedBy(AppUser v){updatedBy=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
