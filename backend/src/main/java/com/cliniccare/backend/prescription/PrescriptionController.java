package com.cliniccare.backend.prescription;

import com.cliniccare.backend.common.ApiResponse;
import com.cliniccare.backend.user.AppUser;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class PrescriptionController {
    private final PrescriptionService prescriptions;
    @Value("${cliniccare.clinic-name:ClinicCare}") private String clinicName;
    @Value("${cliniccare.clinic-contact:}") private String clinicContact;
    public PrescriptionController(PrescriptionService prescriptions){this.prescriptions=prescriptions;}

    @PostMapping("/api/v1/prescriptions") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<PrescriptionDtos.Response> create(@Valid @RequestBody PrescriptionDtos.Request r,Authentication auth){
        return ApiResponse.success("Prescription created",prescriptions.create(r,(AppUser)auth.getDetails()));
    }
    @GetMapping("/api/v1/prescriptions/{id}") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<PrescriptionDtos.Response> get(@PathVariable UUID id){return ApiResponse.success("Prescription",prescriptions.get(id));}
    @GetMapping("/api/v1/patients/{id}/prescriptions") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<List<PrescriptionDtos.Response>> history(@PathVariable UUID id){return ApiResponse.success("Prescription history",prescriptions.history(id));}
    @PutMapping("/api/v1/prescriptions/{id}") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<PrescriptionDtos.Response> update(@PathVariable UUID id,@Valid @RequestBody PrescriptionDtos.Request r,Authentication auth){
        return ApiResponse.success("Prescription updated",prescriptions.update(id,r,(AppUser)auth.getDetails()));
    }
    @PostMapping("/api/v1/prescriptions/{id}/finalize") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<PrescriptionDtos.Response> finalize(@PathVariable UUID id,Authentication auth){
        return ApiResponse.success("Prescription finalized",prescriptions.finalize(id,(AppUser)auth.getDetails()));
    }
    @GetMapping("/api/v1/prescriptions/{id}/pdf") @PreAuthorize("hasRole('DOCTOR')")
    public ResponseEntity<byte[]> pdf(@PathVariable UUID id){
        byte[] pdf=prescriptions.pdf(id,clinicName,clinicContact);
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,"inline; filename=\"prescription-"+id+".pdf\"")
                .contentType(MediaType.APPLICATION_PDF).body(pdf);
    }
}
