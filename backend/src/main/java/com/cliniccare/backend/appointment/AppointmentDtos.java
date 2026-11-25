package com.cliniccare.backend.appointment;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public class AppointmentDtos {
    public record Request(@NotNull UUID patientId,@NotNull UUID doctorId,@NotNull Instant scheduledAt,
                          @Min(5) @Max(240) int durationMinutes,@NotBlank String appointmentType,
                          @Size(max=1000) String notes) {}
    public record RescheduleRequest(@NotNull Instant scheduledAt,@Min(5) @Max(240) int durationMinutes,
                                    @Size(max=1000) String notes) {}
    public record StatusRequest(@NotNull Status status) {}
    public enum Status { SCHEDULED,CONFIRMED,COMPLETED,CANCELLED,NO_SHOW }
    public record Response(UUID id,String appointmentNumber,UUID patientId,UUID doctorId,Instant scheduledAt,
                           int durationMinutes,String appointmentType,String status,String notes) {}
}
