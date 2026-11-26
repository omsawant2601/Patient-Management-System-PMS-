package com.cliniccare.backend.patient;

import com.cliniccare.backend.appointment.AppointmentService;
import com.cliniccare.backend.common.ApiResponse;
import com.cliniccare.backend.consultation.ConsultationService;
import com.cliniccare.backend.prescription.PrescriptionService;
import com.cliniccare.backend.user.AppUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.Map;

@RestController @RequestMapping("/api/v1/patients")
public class PatientController {
    private final PatientService patients;
    private final AppointmentService appointments;
    private final ConsultationService consultations;
    private final PrescriptionService prescriptions;
    public PatientController(PatientService patients,AppointmentService appointments,
                             ConsultationService consultations,PrescriptionService prescriptions){
        this.patients=patients;this.appointments=appointments;this.consultations=consultations;this.prescriptions=prescriptions;
    }

    @PostMapping
    public ApiResponse<PatientDtos.Response> create(@Valid @RequestBody PatientDtos.Request request, Authentication auth) {
        return ApiResponse.success("Patient registered successfully",patients.create(request,(AppUser)auth.getDetails()));
    }
    @GetMapping("/{id}")
    public ApiResponse<PatientDtos.Response> get(@PathVariable UUID id){
        return ApiResponse.success("Patient",patients.get(id));
    }
    @GetMapping
    public ApiResponse<Page<PatientDtos.Response>> list(@RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,@RequestParam(defaultValue="createdAt,desc") String sort,
            @RequestParam(required=false) String name,@RequestParam(required=false) String mobile){
        return ApiResponse.success("Patients",patients.list(page,size,sort,name,mobile));
    }
    @GetMapping("/search")
    public ApiResponse<List<PatientDtos.Response>> search(@RequestParam(required=false) String name,
                                                          @RequestParam(required=false) String mobile){
        return ApiResponse.success("Patient search results",patients.search(name,mobile));
    }
    @PutMapping("/{id}")
    public ApiResponse<PatientDtos.Response> update(@PathVariable UUID id,@Valid @RequestBody PatientDtos.Request request,
                                                    Authentication auth){
        return ApiResponse.success("Patient updated",patients.update(id,request,(AppUser)auth.getDetails()));
    }
    @GetMapping("/{id}/history")
    @Transactional(readOnly=true)
    public ApiResponse<Map<String,Object>> history(@PathVariable UUID id){
        patients.get(id);
        return ApiResponse.success("Patient history",Map.of(
                "patient",patients.get(id),
                "appointments",appointments.history(id),
                "consultations",consultations.history(id),
                "prescriptions",prescriptions.history(id)));
    }
}
