package com.cliniccare.backend.patient;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @Query(value="SELECT nextval('patient_registration_seq')",nativeQuery=true)
    long nextRegistrationNumber();
    Optional<Patient> findByMobile(String mobile);
    Page<Patient> findByFullNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Patient> findByMobileContaining(String mobile, Pageable pageable);
}
