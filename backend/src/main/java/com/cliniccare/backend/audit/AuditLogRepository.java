package com.cliniccare.backend.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.*;
import java.util.UUID;

public interface AuditLogRepository extends JpaRepository<AuditLog,UUID> {
    Page<AuditLog> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
