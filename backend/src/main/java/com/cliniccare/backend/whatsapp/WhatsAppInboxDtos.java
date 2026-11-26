package com.cliniccare.backend.whatsapp;

import java.time.Instant;
import java.util.UUID;

public final class WhatsAppInboxDtos {
    private WhatsAppInboxDtos() {}
    public record Message(UUID id,String senderPhone,String senderName,String messageType,
                          String content,Instant receivedAt) {}
}
