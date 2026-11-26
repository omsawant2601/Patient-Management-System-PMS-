package com.cliniccare.backend.patient;

import java.text.Normalizer;
import java.util.Locale;

public final class PatientDocumentNumber {
    private PatientDocumentNumber() {}

    public static String format(String prefix, Patient patient, long serial, boolean disambiguateName) {
        String slug = slug(patient.getFullName());
        String patientSuffix = disambiguateName ? "-P" + patient.getPatientCode() : "";
        return prefix + "-" + slug + patientSuffix + "-" + serial;
    }

    static String slug(String fullName) {
        String slug = Normalizer.normalize(fullName, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (slug.isEmpty()) slug = "patient";
        if (slug.length() > 80) slug = slug.substring(0, 80).replaceAll("-+$", "");
        return slug;
    }
}
