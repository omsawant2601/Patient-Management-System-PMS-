package com.cliniccare.backend;

import com.cliniccare.backend.patient.Patient;
import com.cliniccare.backend.patient.PatientDtos;
import com.cliniccare.backend.patient.PatientRepository;
import com.cliniccare.backend.patient.PatientService;
import com.cliniccare.backend.user.AppUser;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PatientServiceTests {
    @Test
    void assignsNextSequentialRegistrationNumber() {
        PatientRepository patients = mock(PatientRepository.class);
        when(patients.findByMobile("9876543210")).thenReturn(Optional.empty());
        when(patients.nextRegistrationNumber()).thenReturn(1L);
        when(patients.save(any(Patient.class))).thenAnswer(invocation -> invocation.getArgument(0));
        PatientService service = new PatientService(patients);

        PatientDtos.Response response = service.create(
                new PatientDtos.Request("Test Patient", "9876543210", null, null, null, null),
                new AppUser());

        assertEquals("1", response.patientCode());
        verify(patients).nextRegistrationNumber();
    }
}
