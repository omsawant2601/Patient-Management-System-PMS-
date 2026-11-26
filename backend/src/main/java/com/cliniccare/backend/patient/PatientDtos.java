package com.cliniccare.backend.patient;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;

public class PatientDtos {
    public record Request(@NotBlank @Size(max=150) String fullName,
                          @NotBlank @Pattern(regexp="^[6-9][0-9]{9}$") String mobile,
                          @Pattern(regexp="^($|[6-9][0-9]{9}$)") String alternateContactNumber,
                          @Size(max=1000) String address,
                          @Past LocalDate dateOfBirth,
                          @Size(max=20) String gender) {}
    public record Response(UUID id,String patientCode,String fullName,String mobile,String alternateContactNumber,
                           String address,LocalDate dateOfBirth,Integer age,String gender,
                           LocalDate registrationDate,boolean hasProfilePhoto) {}
}
