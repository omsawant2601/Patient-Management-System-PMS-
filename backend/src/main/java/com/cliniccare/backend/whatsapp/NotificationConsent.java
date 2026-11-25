package com.cliniccare.backend.whatsapp;

import com.cliniccare.backend.patient.Patient;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="notification_consents")
public class NotificationConsent {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="patient_id",nullable=false) private Patient patient;
    @Column(nullable=false) private String channel="WHATSAPP";
    @Column(name="consent_given",nullable=false) private boolean consentGiven;
    @Column(name="consent_status",nullable=false) private String consentStatus="DENIED";
    @Column(name="consent_at") private Instant consentAt;
    @Column(name="revoked_at") private Instant revokedAt;
    @Column(name="consent_source") private String consentSource;
    @Column(name="contact_number") private String contactNumber;
    public UUID getId(){return id;} public void setId(UUID v){id=v;}
    public Patient getPatient(){return patient;} public void setPatient(Patient v){patient=v;}
    public String getChannel(){return channel;} public void setChannel(String v){channel=v;}
    public boolean isConsentGiven(){return consentGiven;} public void setConsentGiven(boolean v){consentGiven=v;}
    public String getConsentStatus(){return consentStatus;} public void setConsentStatus(String v){consentStatus=v;}
    public Instant getConsentAt(){return consentAt;} public void setConsentAt(Instant v){consentAt=v;}
    public Instant getRevokedAt(){return revokedAt;} public void setRevokedAt(Instant v){revokedAt=v;}
    public String getConsentSource(){return consentSource;} public void setConsentSource(String v){consentSource=v;}
    public String getContactNumber(){return contactNumber;} public void setContactNumber(String v){contactNumber=v;}
}
