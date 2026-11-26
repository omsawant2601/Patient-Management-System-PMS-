package com.cliniccare.backend.whatsapp;

import com.fasterxml.jackson.databind.JsonNode;
import com.cliniccare.backend.reminder.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DateTimeException;
import java.time.Instant;
import java.util.UUID;

@Service
public class WhatsAppWebhookService {
    private final AppointmentReminderRepository reminders;
    private final WhatsAppInboundMessageRepository inboundMessages;
    public WhatsAppWebhookService(AppointmentReminderRepository reminders,WhatsAppInboundMessageRepository inboundMessages){
        this.reminders=reminders;this.inboundMessages=inboundMessages;
    }

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
                    reminders.findByProviderMessageId(messageId).ifPresent(r->{
                            String state=status.path("status").asText("");
                            if("sent".equals(state))r.setStatus("SENT");
                            else if("delivered".equals(state))r.setStatus("DELIVERED");
                            else if("read".equals(state))r.setStatus("READ");
                            else if("failed".equals(state))r.setStatus("FAILED");
                        });
                }
                JsonNode messages=value.path("messages");
                if(messages.isArray())for(JsonNode message:messages)storeIncomingMessage(message,value.path("contacts"));
            }
    }

    private void storeIncomingMessage(JsonNode message,JsonNode contacts) {
        String messageId=message.path("id").asText("");
        String sender=message.path("from").asText("");
        if(messageId.isBlank()||sender.isBlank())return;
        String type=message.path("type").asText("unknown");
        String senderName=findSenderName(contacts,sender);
        inboundMessages.insertIfAbsent(UUID.randomUUID(),messageId,sender,senderName,type,
                extractContent(message,type),receivedAt(message.path("timestamp")));
    }

    private String findSenderName(JsonNode contacts,String sender) {
        if(!contacts.isArray())return null;
        for(JsonNode contact:contacts)
            if(sender.equals(contact.path("wa_id").asText()))
                return contact.path("profile").path("name").asText(null);
        return null;
    }

    private String extractContent(JsonNode message,String type) {
        return switch(type) {
            case "text" -> message.path("text").path("body").asText("");
            case "interactive" -> {
                JsonNode interactive=message.path("interactive");
                String title=interactive.path("button_reply").path("title").asText("");
                if(title.isBlank())title=interactive.path("list_reply").path("title").asText("");
                yield title.isBlank()?"Interactive message":title;
            }
            case "button" -> message.path("button").path("text").asText("Button reply");
            case "location" -> message.path("location").path("name").asText("Location shared");
            case "contacts" -> "Contact shared";
            default -> {
                String caption=message.path(type).path("caption").asText("");
                yield caption.isBlank()?type.substring(0,1).toUpperCase()+type.substring(1)+" message":caption;
            }
        };
    }

    private Instant receivedAt(JsonNode timestamp) {
        if(timestamp.isMissingNode()||timestamp.asText().isBlank())return Instant.now();
        try {
            return Instant.ofEpochSecond(Long.parseLong(timestamp.asText()));
        } catch(NumberFormatException|DateTimeException ex) {
            return Instant.now();
        }
    }
}
