package com.cliniccare.backend;

import com.cliniccare.backend.security.JwtService;
import com.cliniccare.backend.user.AppUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AuthUnitTests {
    @Test
    void bcryptHashesAndMatchesPasswords(){
        var encoder=new BCryptPasswordEncoder();
        String hash=encoder.encode("clinic-password-123");
        assertNotEquals("clinic-password-123",hash);
        assertTrue(encoder.matches("clinic-password-123",hash));
        assertFalse(encoder.matches("wrong-password",hash));
    }

    @Test
    void jwtContainsRoleAndExpires(){
        var jwt=new JwtService("cliniccare-test-secret-that-is-at-least-48-characters-long",15);
        AppUser u=new AppUser();
        u.setId(UUID.randomUUID());u.setUsername("doctor");u.setRole(AppUser.Role.DOCTOR);
        String token=jwt.generateAccessToken(u);
        var claims=jwt.parse(token);
        assertEquals("DOCTOR",claims.get("role"));
        assertEquals(u.getId().toString(),claims.getSubject());
        assertTrue(claims.getExpiration().toInstant().isAfter(java.time.Instant.now()));
    }
}
