package com.cliniccare.backend.whatsapp;

import com.cliniccare.backend.common.*;
import com.cliniccare.backend.patient.PatientRepository;
import jakarta.validation.constraints.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/whatsapp")
public class ConsentController {
    private final NotificationConsentRepository consents;
    private final PatientRepository patients;
    public ConsentController(NotificationConsentRepository consents,PatientRepository patients){this.consents=consents;this.patients=patients;}

    public record ConsentRequest(@NotNull UUID patientId,@Pattern(regexp="^[6-9][0-9]{9}$") String contactNumber,
                                 @NotNull Boolean consentGiven,@NotBlank String source){}
    public record ConsentResponse(UUID id,UUID patientId,boolean consentGiven,String consentStatus,String contactNumber){}

    @PostMapping("/consent")
    @Transactional
    public ApiResponse<ConsentResponse> save(@RequestBody ConsentRequest r){
        var patient=patients.findById(r.patientId()).orElseThrow(() -> new NotFoundException("Patient not found"));
        var consent=consents.findByPatientIdAndChannel(patient.getId(),"WHATSAPP").orElse(new NotificationConsent());
        consent.setPatient(patient);consent.setChannel("WHATSAPP");consent.setConsentGiven(r.consentGiven());
        consent.setConsentStatus(r.consentGiven()?"GRANTED":"REVOKED");consent.setConsentSource(r.source());
        consent.setContactNumber(r.contactNumber()==null?patient.getMobile():r.contactNumber());
        if(r.consentGiven()){consent.setConsentAt(Instant.now());consent.setRevokedAt(null);}
        else{consent.setRevokedAt(Instant.now());}
        consents.save(consent);
        return ApiResponse.success("WhatsApp consent updated",new ConsentResponse(consent.getId(),patient.getId(),
                consent.isConsentGiven(),consent.getConsentStatus(),consent.getContactNumber()));
    }
}
