package com.cliniccare.backend.appointment;

import com.cliniccare.backend.common.ApiResponse;
import com.cliniccare.backend.user.AppUser;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController @RequestMapping("/api/v1/appointments")
public class AppointmentController {
    private final AppointmentService appointments;
    public AppointmentController(AppointmentService appointments){this.appointments=appointments;}
    @PostMapping
    public ApiResponse<AppointmentDtos.Response> schedule(@Valid @RequestBody AppointmentDtos.Request r,Authentication auth){
        return ApiResponse.success("Appointment scheduled",appointments.schedule(r,(AppUser)auth.getDetails()));
    }
    @GetMapping
    public ApiResponse<Page<AppointmentDtos.Response>> list(@RequestParam(defaultValue="0") int page,
            @RequestParam(defaultValue="20") int size,@RequestParam(required=false) UUID patientId,
            @RequestParam(required=false) UUID doctorId,@RequestParam(required=false) Instant from,
            @RequestParam(required=false) Instant to){
        return ApiResponse.success("Appointments",appointments.list(page,size,patientId,doctorId,from,to));
    }
    @GetMapping("/{id}")
    public ApiResponse<AppointmentDtos.Response> get(@PathVariable UUID id){return ApiResponse.success("Appointment",appointments.get(id));}
    @PutMapping("/{id}")
    public ApiResponse<AppointmentDtos.Response> reschedule(@PathVariable UUID id,@Valid @RequestBody AppointmentDtos.RescheduleRequest r,Authentication auth){
        return ApiResponse.success("Appointment rescheduled",appointments.reschedule(id,r,(AppUser)auth.getDetails()));
    }
    @PatchMapping("/{id}/cancel")
    public ApiResponse<AppointmentDtos.Response> cancel(@PathVariable UUID id,Authentication auth){
        return ApiResponse.success("Appointment cancelled",appointments.cancel(id,(AppUser)auth.getDetails()));
    }
    @PatchMapping("/{id}/status")
    public ApiResponse<AppointmentDtos.Response> status(@PathVariable UUID id,@Valid @RequestBody AppointmentDtos.StatusRequest r,Authentication auth){
        return ApiResponse.success("Appointment status updated",appointments.status(id,r,(AppUser)auth.getDetails()));
    }
}
