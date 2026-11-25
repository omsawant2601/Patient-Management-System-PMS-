package com.cliniccare.backend.whatsapp;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface NotificationConsentRepository extends JpaRepository<NotificationConsent,UUID> {
    Optional<NotificationConsent> findByPatientIdAndChannel(UUID patientId,String channel);
}
