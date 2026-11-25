package com.cliniccare.backend.reminder;

import com.cliniccare.backend.appointment.Appointment;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="appointment_reminders")
public class AppointmentReminder {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="appointment_id",nullable=false) private Appointment appointment;
    @Column(name="reminder_type",nullable=false) private String reminderType;
    @Column(name="scheduled_at",nullable=false) private Instant scheduledAt;
    @Column(name="sent_at") private Instant sentAt;
    @Column(name="last_attempt_at") private Instant lastAttemptAt;
    @Column(name="next_attempt_at") private Instant nextAttemptAt;
    @Column(nullable=false) private String status="PENDING";
    @Column(name="provider_message_id") private String providerMessageId;
    @Column(name="retry_count",nullable=false) private int retryCount;
    @Column(name="error_message") private String errorMessage;
    @Version private Long version;
    public UUID getId(){return id;} public void setId(UUID v){id=v;}
    public Appointment getAppointment(){return appointment;} public void setAppointment(Appointment v){appointment=v;}
    public String getReminderType(){return reminderType;} public void setReminderType(String v){reminderType=v;}
    public Instant getScheduledAt(){return scheduledAt;} public void setScheduledAt(Instant v){scheduledAt=v;}
    public Instant getSentAt(){return sentAt;} public void setSentAt(Instant v){sentAt=v;}
    public Instant getLastAttemptAt(){return lastAttemptAt;} public void setLastAttemptAt(Instant v){lastAttemptAt=v;}
    public Instant getNextAttemptAt(){return nextAttemptAt;} public void setNextAttemptAt(Instant v){nextAttemptAt=v;}
    public String getStatus(){return status;} public void setStatus(String v){status=v;}
    public String getProviderMessageId(){return providerMessageId;} public void setProviderMessageId(String v){providerMessageId=v;}
    public int getRetryCount(){return retryCount;} public void setRetryCount(int v){retryCount=v;}
    public String getErrorMessage(){return errorMessage;} public void setErrorMessage(String v){errorMessage=v;}
}
