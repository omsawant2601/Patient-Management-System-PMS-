package com.cliniccare.backend.patient;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import java.util.Optional;
import java.util.UUID;

public interface PatientRepository extends JpaRepository<Patient, UUID> {
    @Query(value="SELECT nextval('patient_registration_seq')",nativeQuery=true)
    long nextRegistrationNumber();
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select p from Patient p where p.id = :id")
    Optional<Patient> findByIdForUpdate(@Param("id") UUID id);
    Optional<Patient> findByMobile(String mobile);
    Page<Patient> findByFullNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Patient> findByMobileContaining(String mobile, Pageable pageable);
}
