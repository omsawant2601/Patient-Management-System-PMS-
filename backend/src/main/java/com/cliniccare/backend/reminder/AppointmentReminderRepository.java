package com.cliniccare.backend.reminder;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AppointmentReminderRepository extends JpaRepository<AppointmentReminder,UUID> {
    List<AppointmentReminder> findByAppointmentId(UUID appointmentId);
    Optional<AppointmentReminder> findByAppointmentIdAndReminderType(UUID appointmentId,String reminderType);
    Optional<AppointmentReminder> findByProviderMessageId(String providerMessageId);
    List<AppointmentReminder> findByStatusInAndScheduledAtBefore(List<String> statuses,java.time.Instant time);
}
