package com.cliniccare.backend.patient;

import java.time.Instant;
import java.util.UUID;

public final class PatientImageDtos {
    private PatientImageDtos() {}
    public record Comparison(UUID id,String caption,Instant createdAt) {}
}
