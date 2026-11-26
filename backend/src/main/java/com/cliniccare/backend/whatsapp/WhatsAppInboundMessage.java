package com.cliniccare.backend.whatsapp;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name="whatsapp_inbound_messages")
public class WhatsAppInboundMessage {
    @Id private UUID id;
    @Column(name="provider_message_id",nullable=false,unique=true,length=150) private String providerMessageId;
    @Column(name="sender_phone",nullable=false,length=32) private String senderPhone;
    @Column(name="sender_name",length=150) private String senderName;
    @Column(name="message_type",nullable=false,length=40) private String messageType;
    @Column(nullable=false,columnDefinition="TEXT") private String content;
    @Column(name="received_at",nullable=false) private Instant receivedAt;

    public UUID getId(){return id;} public void setId(UUID value){id=value;}
    public String getProviderMessageId(){return providerMessageId;} public void setProviderMessageId(String value){providerMessageId=value;}
    public String getSenderPhone(){return senderPhone;} public void setSenderPhone(String value){senderPhone=value;}
    public String getSenderName(){return senderName;} public void setSenderName(String value){senderName=value;}
    public String getMessageType(){return messageType;} public void setMessageType(String value){messageType=value;}
    public String getContent(){return content;} public void setContent(String value){content=value;}
    public Instant getReceivedAt(){return receivedAt;} public void setReceivedAt(Instant value){receivedAt=value;}
}
