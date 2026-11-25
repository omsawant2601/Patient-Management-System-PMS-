package com.cliniccare.backend.user;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "app_users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.UUID) private UUID id;
    @Column(name="full_name", nullable=false) private String fullName;
    @Column(nullable=false, unique=true) private String username;
    @Column(length=254) private String email;
    @Column(nullable=false, unique=true) private String mobile;
    @Column(name="password_hash", nullable=false) private String passwordHash;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private Role role;
    @Column(nullable=false) private boolean active = true;
    @Column(name="failed_login_attempts", nullable=false) private int failedLoginAttempts;
    @Column(name="locked_until") private Instant lockedUntil;
    @Column(name="last_login_at") private Instant lastLoginAt;
    @Column(name="created_at", nullable=false, updatable=false) private Instant createdAt = Instant.now();
    @Column(name="updated_at", nullable=false) private Instant updatedAt = Instant.now();

    public enum Role { DOCTOR, RECEPTIONIST }
    public UUID getId(){return id;} public void setId(UUID id){this.id=id;}
    public String getFullName(){return fullName;} public void setFullName(String v){fullName=v;}
    public String getUsername(){return username;} public void setUsername(String v){username=v;}
    public String getEmail(){return email;} public void setEmail(String v){email=v;}
    public String getMobile(){return mobile;} public void setMobile(String v){mobile=v;}
    public String getPasswordHash(){return passwordHash;} public void setPasswordHash(String v){passwordHash=v;}
    public Role getRole(){return role;} public void setRole(Role v){role=v;}
    public boolean isActive(){return active;} public void setActive(boolean v){active=v;}
    public int getFailedLoginAttempts(){return failedLoginAttempts;} public void setFailedLoginAttempts(int v){failedLoginAttempts=v;}
    public Instant getLockedUntil(){return lockedUntil;} public void setLockedUntil(Instant v){lockedUntil=v;}
    public Instant getLastLoginAt(){return lastLoginAt;} public void setLastLoginAt(Instant v){lastLoginAt=v;}
    public Instant getCreatedAt(){return createdAt;} public void setCreatedAt(Instant v){createdAt=v;}
    public Instant getUpdatedAt(){return updatedAt;} public void setUpdatedAt(Instant v){updatedAt=v;}
}
