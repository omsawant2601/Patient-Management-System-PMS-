package com.cliniccare.backend.consultation;

import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.UUID;

public class ConsultationDtos {
    public record Request(@NotNull UUID patientId, UUID appointmentId, Instant consultationDate,
                          @NotBlank @Size(max=3000) String symptoms, @Size(max=3000) String diagnosis,
                          @Size(max=5000) String clinicalNotes, @Size(max=3000) String followUpRecommendations) {}
    public record Response(UUID id, UUID patientId, UUID doctorId, UUID appointmentId, Instant consultationDate,
                           String symptoms, String diagnosis, String clinicalNotes, String followUpRecommendations,
                           String status) {}
}
