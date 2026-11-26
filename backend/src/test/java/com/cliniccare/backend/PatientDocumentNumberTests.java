package com.cliniccare.backend;

import com.cliniccare.backend.patient.Patient;
import com.cliniccare.backend.patient.PatientDocumentNumber;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PatientDocumentNumberTests {
    @Test
    void createsReadablePerPatientSerialsAndDisambiguatesDuplicateNames() {
        Patient patient=new Patient();
        patient.setFullName("Omkar Sawant");
        patient.setPatientCode("12");

        assertEquals("APT-omkar-sawant-1",PatientDocumentNumber.format("APT",patient,1,false));
        assertEquals("RX-omkar-sawant-P12-2",PatientDocumentNumber.format("RX",patient,2,true));
    }

    @Test
    void normalizesUnicodeAndFallsBackForNamesWithoutLatinCharacters() {
        Patient accented=new Patient();
        accented.setFullName("Éva O'Neil");
        assertEquals("APT-eva-o-neil-1",PatientDocumentNumber.format("APT",accented,1,false));

        Patient nonLatin=new Patient();
        nonLatin.setFullName("患者");
        assertEquals("RX-patient-1",PatientDocumentNumber.format("RX",nonLatin,1,false));
    }
}
