package com.cliniccare.backend;

import com.cliniccare.backend.reminder.AppointmentReminderRepository;
import com.cliniccare.backend.whatsapp.WhatsAppInboundMessageRepository;
import com.cliniccare.backend.whatsapp.WhatsAppWebhookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class WhatsAppWebhookServiceTests {
    @Test
    void persistsIncomingTextMessageOnceWithReadableSenderDetails() throws Exception {
        AppointmentReminderRepository reminders=mock(AppointmentReminderRepository.class);
        WhatsAppInboundMessageRepository messages=mock(WhatsAppInboundMessageRepository.class);
        WhatsAppWebhookService service=new WhatsAppWebhookService(reminders,messages);
        String json="""
                {"entry":[{"changes":[{"value":{
                  "contacts":[{"wa_id":"15551234567","profile":{"name":"Clinic user"}}],
                  "messages":[{"id":"wamid.test-message","from":"15551234567","timestamp":"1730000000",
                    "type":"text","text":{"body":"Hello clinic"}}]
                }}]}]}
                """;

        service.process(new ObjectMapper().readTree(json));

        verify(messages).insertIfAbsent(any(),eq("wamid.test-message"),eq("15551234567"),
                eq("Clinic user"),eq("text"),eq("Hello clinic"),any());
        verifyNoInteractions(reminders);
    }
}
