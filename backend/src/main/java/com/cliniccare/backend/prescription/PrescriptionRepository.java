package com.cliniccare.backend.prescription;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface PrescriptionRepository extends JpaRepository<Prescription,UUID> {
    List<Prescription> findByPatientIdOrderByPrescriptionDateDesc(UUID patientId);
    long countByPatientId(UUID patientId);
    boolean existsByPrescriptionNumber(String prescriptionNumber);
}
