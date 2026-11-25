package com.cliniccare.backend.whatsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.cliniccare.backend.reminder.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WhatsAppWebhookService {
    private final AppointmentReminderRepository reminders;
    public WhatsAppWebhookService(AppointmentReminderRepository reminders){this.reminders=reminders;}

    @Transactional
    public void process(JsonNode payload){
        JsonNode entries=payload.path("entry");
        if(entries.isArray()) for(JsonNode entry:entries)
            for(JsonNode change:entry.path("changes")){
                JsonNode value=change.path("value");
                JsonNode statuses=value.path("statuses");
                if(statuses.isArray()) for(JsonNode status:statuses){
                    String messageId=status.path("id").asText(null);
                    if(messageId==null)continue;
                    reminders.findAll().stream()
                        .filter(r->messageId.equals(r.getProviderMessageId()))
                        .findFirst().ifPresent(r->{
                            String state=status.path("status").asText("");
                            if("sent".equals(state))r.setStatus("SENT");
                            else if("delivered".equals(state))r.setStatus("DELIVERED");
                            else if("read".equals(state))r.setStatus("READ");
                            else if("failed".equals(state))r.setStatus("FAILED");
                        });
                }
            }
    }
}
