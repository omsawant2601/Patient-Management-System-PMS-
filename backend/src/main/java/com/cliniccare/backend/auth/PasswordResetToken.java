package com.cliniccare.backend.auth;

import com.cliniccare.backend.user.AppUser;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name="password_reset_tokens")
public class PasswordResetToken {
    @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id;
    @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="user_id", nullable=false) private AppUser user;
    @Column(name="token_hash", nullable=false, unique=true) private String tokenHash;
    @Column(name="expires_at", nullable=false) private Instant expiresAt;
    @Column(name="used_at") private Instant usedAt;
    @Column(name="created_at", nullable=false) private Instant createdAt=Instant.now();
    public UUID getId(){return id;} public void setId(UUID v){id=v;}
    public AppUser getUser(){return user;} public void setUser(AppUser v){user=v;}
    public String getTokenHash(){return tokenHash;} public void setTokenHash(String v){tokenHash=v;}
    public Instant getExpiresAt(){return expiresAt;} public void setExpiresAt(Instant v){expiresAt=v;}
    public Instant getUsedAt(){return usedAt;} public void setUsedAt(Instant v){usedAt=v;}
}
