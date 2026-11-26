package com.cliniccare.backend.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PatientPhotoComparisonRepository extends JpaRepository<PatientPhotoComparison,UUID> {
    List<PatientPhotoComparison> findByPatientIdOrderByCreatedAtDesc(UUID patientId);
    Optional<PatientPhotoComparison> findByIdAndPatientId(UUID id,UUID patientId);
}
