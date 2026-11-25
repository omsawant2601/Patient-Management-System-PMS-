package com.cliniccare.backend.audit;

import com.cliniccare.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id") private AppUser user;
    @Column(nullable=false) private String action;
    @Column(name="entity_type") private String entityType;
    @Column(name="entity_id") private UUID entityId;
    @Column(name="ip_address") private String ipAddress;
    @Column(columnDefinition="jsonb") private String details;
    @Column(name="created_at",nullable=false) private Instant createdAt=Instant.now();
    public UUID getId(){return id;} public void setId(UUID v){id=v;}
    public AppUser getUser(){return user;} public void setUser(AppUser v){user=v;}
    public String getAction(){return action;} public void setAction(String v){action=v;}
    public String getEntityType(){return entityType;} public void setEntityType(String v){entityType=v;}
    public UUID getEntityId(){return entityId;} public void setEntityId(UUID v){entityId=v;}
    public String getIpAddress(){return ipAddress;} public void setIpAddress(String v){ipAddress=v;}
    public String getDetails(){return details;} public void setDetails(String v){details=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
}
