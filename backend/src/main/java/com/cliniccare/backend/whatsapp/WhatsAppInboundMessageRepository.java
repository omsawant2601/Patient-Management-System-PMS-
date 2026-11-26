package com.cliniccare.backend.whatsapp;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface WhatsAppInboundMessageRepository extends JpaRepository<WhatsAppInboundMessage,UUID> {
    Page<WhatsAppInboundMessage> findAllByOrderByReceivedAtDesc(Pageable pageable);

    @Modifying
    @Query(value="""
        INSERT INTO whatsapp_inbound_messages
            (id,provider_message_id,sender_phone,sender_name,message_type,content,received_at)
        VALUES
            (:id,:providerMessageId,:senderPhone,:senderName,:messageType,:content,:receivedAt)
        ON CONFLICT (provider_message_id) DO NOTHING
        """,nativeQuery=true)
    int insertIfAbsent(@Param("id") UUID id,@Param("providerMessageId") String providerMessageId,
                       @Param("senderPhone") String senderPhone,@Param("senderName") String senderName,
                       @Param("messageType") String messageType,@Param("content") String content,
                       @Param("receivedAt") Instant receivedAt);
}
