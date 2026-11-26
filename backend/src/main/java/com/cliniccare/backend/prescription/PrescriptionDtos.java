package com.cliniccare.backend.prescription;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.*;

public class PrescriptionDtos {
    public record Medicine(@NotBlank @Size(max=200) String medicineName,
                           @Size(max=100) String dosage,@Size(max=100) String frequency,
                           @Size(max=100) String duration,@Size(max=50) String quantity,
                           @Size(max=500) String instructions) {}
    public record Request(@NotNull UUID consultationId,@Size(max=5000) String instructions,
                          @NotEmpty List<@Valid Medicine> medicines) {}
    public record Response(UUID id,String prescriptionNumber,UUID patientId,UUID doctorId,
                           UUID consultationId,Instant prescriptionDate,String instructions,
                           String status,List<Medicine> medicines,String doctorName) {}
}
