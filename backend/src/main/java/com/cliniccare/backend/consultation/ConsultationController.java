package com.cliniccare.backend.consultation;

import com.cliniccare.backend.common.ApiResponse;
import com.cliniccare.backend.user.AppUser;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
public class ConsultationController {
    private final ConsultationService consultations;
    public ConsultationController(ConsultationService consultations){this.consultations=consultations;}

    @PostMapping("/api/v1/consultations") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<ConsultationDtos.Response> create(@Valid @RequestBody ConsultationDtos.Request r, Authentication auth){
        return ApiResponse.success("Consultation created",consultations.create(r,(AppUser)auth.getDetails()));
    }
    @GetMapping("/api/v1/consultations/{id}") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<ConsultationDtos.Response> get(@PathVariable UUID id){return ApiResponse.success("Consultation",consultations.get(id));}
    @GetMapping("/api/v1/patients/{id}/consultations") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<List<ConsultationDtos.Response>> history(@PathVariable UUID id){return ApiResponse.success("Consultation history",consultations.history(id));}
    @PutMapping("/api/v1/consultations/{id}") @PreAuthorize("hasRole('DOCTOR')")
    public ApiResponse<ConsultationDtos.Response> update(@PathVariable UUID id,@Valid @RequestBody ConsultationDtos.Request r,Authentication auth){
        return ApiResponse.success("Consultation updated",consultations.update(id,r,(AppUser)auth.getDetails()));
    }
}
