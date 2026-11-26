package com.cliniccare.backend.reminder;

import com.cliniccare.backend.common.ApiResponse;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RestController
public class ReminderController {
    private final AppointmentReminderRepository reminders;
    public ReminderController(AppointmentReminderRepository reminders){this.reminders=reminders;}
    @GetMapping("/api/v1/reminders")
    @Transactional(readOnly=true)
    public ApiResponse<List<ReminderDto>> list(){return ApiResponse.success("Reminders",reminders.findAll().stream().map(ReminderDto::from).toList());}
    @GetMapping("/api/v1/appointments/{id}/reminders")
    @Transactional(readOnly=true)
    public ApiResponse<List<ReminderDto>> forAppointment(@PathVariable UUID id){
        return ApiResponse.success("Appointment reminders",reminders.findByAppointmentId(id).stream().map(ReminderDto::from).toList());
    }
    public record ReminderDto(UUID id,UUID appointmentId,String appointmentNumber,UUID patientId,String patientName,String patientCode,
                              String reminderType,java.time.Instant scheduledAt,
                              String status,String providerMessageId,int retryCount,java.time.Instant sentAt,String errorMessage){
        static ReminderDto from(AppointmentReminder r){var appointment=r.getAppointment();var patient=appointment.getPatient();
            return new ReminderDto(r.getId(),appointment.getId(),appointment.getAppointmentNumber(),patient.getId(),patient.getFullName(),
                    patient.getPatientCode(),r.getReminderType(),r.getScheduledAt(),r.getStatus(),
                    r.getProviderMessageId(),r.getRetryCount(),r.getSentAt(),r.getErrorMessage());}
    }
}
