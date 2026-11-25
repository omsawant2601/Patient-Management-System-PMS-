package com.cliniccare.backend.patient;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    Optional<Patient> findByMobile(String mobile);
    Page<Patient> findByFullNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Patient> findByMobileContaining(String mobile, Pageable pageable);
}
