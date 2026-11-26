package com.cliniccare.backend.reminder;

import com.cliniccare.backend.whatsapp.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.time.Instant;
import java.util.*;

@Component
public class ReminderScheduler {
    private final AppointmentReminderRepository reminders;
    private final NotificationConsentRepository consents;
    @Value("${whatsapp.access-token:}") String accessToken;
    @Value("${whatsapp.phone-number-id:}") String phoneNumberId;
    @Value("${whatsapp.graph-version:v22.0}") String graphVersion;
    @Value("${whatsapp.template-name:appointment_reminder}") String templateName;

    public ReminderScheduler(AppointmentReminderRepository reminders,NotificationConsentRepository consents){
        this.reminders=reminders;this.consents=consents;
    }

    @Scheduled(fixedDelay=60000)
    @Transactional
    public void processDueReminders(){
        Instant now=Instant.now();
        for(AppointmentReminder r:reminders.findByStatusInAndScheduledAtBefore(List.of("PENDING","FAILED"),now)){
            if(r.getNextAttemptAt()!=null&&r.getNextAttemptAt().isAfter(now))continue;
            if(Set.of("CANCELLED","COMPLETED","NO_SHOW").contains(r.getAppointment().getStatus())){
                r.setStatus("CANCELLED");continue;
            }
            if(r.getAppointment().getScheduledAt().isBefore(now)){r.setStatus("CANCELLED");continue;}
            r.setStatus("PROCESSING");r.setLastAttemptAt(now);
            processOne(r);
        }
    }

    private void processOne(AppointmentReminder r){
        var appointment=r.getAppointment();
        var consent=consents.findByPatientIdAndChannel(appointment.getPatient().getId(),"WHATSAPP").orElse(null);
        if(consent==null||!consent.isConsentGiven()||!"GRANTED".equals(consent.getConsentStatus())){
            r.setStatus("CANCELLED");r.setErrorMessage("Valid WhatsApp consent not present");return;
        }
        if(accessToken.isBlank()||phoneNumberId.isBlank()){
            r.setStatus("FAILED");r.setErrorMessage("WhatsApp Cloud API credentials are not configured");
            r.setRetryCount(r.getRetryCount()+1);r.setNextAttemptAt(Instant.now().plusSeconds(900));return;
        }
        try{
            String patientName=appointment.getPatient().getFullName();
            String date=appointment.getScheduledAt().toString();
            String time=appointment.getScheduledAt().toString().substring(11,16);
            Map<String,Object> template=Map.of("name",templateName,"language",Map.of("code","en"),
                    "components",List.of(Map.of("type","body","parameters",
                            List.of(Map.of("type","text","text",patientName),
                                    Map.of("type","text","text",date),
                                    Map.of("type","text","text",time)))));
            Map<String,Object> body=Map.of("messaging_product","whatsapp","to",consent.getContactNumber(),
                    "type","template","template",template);
            var response=RestClient.create("https://graph.facebook.com/"+graphVersion+"/"+phoneNumberId+"/messages")
                    .post().header("Authorization","Bearer "+accessToken).body(body).retrieve()
                    .toEntity(Map.class);
            Object id=response.getBody()==null?null:((Map<?,?>)response.getBody()).get("messages");
            String messageId=null;
            if(id instanceof List<?> messages&&!messages.isEmpty()&&messages.get(0) instanceof Map<?,?> m)
                messageId=String.valueOf(m.get("id"));
            if(messageId==null)throw new IllegalStateException("Meta API did not return a message ID");
            r.setProviderMessageId(messageId);r.setStatus("SENT");r.setSentAt(Instant.now());r.setErrorMessage(null);
        }catch(Exception ex){
            r.setRetryCount(r.getRetryCount()+1);
            r.setErrorMessage(ex.getMessage()==null?"WhatsApp send failed":ex.getMessage());
            r.setStatus(r.getRetryCount()>=5?"FAILED":"FAILED");
            if(r.getRetryCount()>=5) {
                r.setNextAttemptAt(Instant.now().plusSeconds(315360000L));
            } else {
                long backoff=Math.min(3600L, (long)Math.pow(5,r.getRetryCount())*60L);
                r.setNextAttemptAt(Instant.now().plusSeconds(backoff));
            }
        }
    }
}
