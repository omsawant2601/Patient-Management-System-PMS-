package com.cliniccare.backend.auth;

import com.cliniccare.backend.common.ConflictException;
import com.cliniccare.backend.common.NotFoundException;
import com.cliniccare.backend.common.RecoveryUnavailableException;
import com.cliniccare.backend.security.JwtService;
import com.cliniccare.backend.user.AppUser;
import com.cliniccare.backend.user.AppUserRepository;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;
import java.net.URLEncoder;

@Service
public class AuthService {
    private final AppUserRepository users;
    private final RefreshTokenRepository refreshTokens;
    private final PasswordResetTokenRepository resetTokens;
    private final ObjectProvider<PasswordResetDelivery> resetDelivery;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private final SecureRandom random = new SecureRandom();
    private final long refreshDays;
    private final String frontendUrl;

    public AuthService(AppUserRepository users, RefreshTokenRepository refreshTokens,
                       PasswordResetTokenRepository resetTokens,
                       ObjectProvider<PasswordResetDelivery> resetDelivery, JwtService jwtService,
                       @Value("${cliniccare.jwt.refresh-days:7}") long refreshDays,
                       @Value("${cliniccare.frontend-url:http://localhost:5173}") String frontendUrl) {
        this.users=users; this.refreshTokens=refreshTokens; this.resetTokens=resetTokens;
        this.resetDelivery=resetDelivery; this.jwtService=jwtService; this.refreshDays=refreshDays;
        this.frontendUrl=frontendUrl.replaceAll("/+$", "");
    }

    @Transactional(noRollbackFor = BadCredentialsException.class)
    public AuthDtos.TokenResponse login(AuthDtos.LoginRequest request) {
        AppUser user=users.findByUsernameIgnoreCase(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));
        if (!user.isActive()) throw new BadCredentialsException("Account inactive");
        if (user.getLockedUntil()!=null && user.getLockedUntil().isAfter(Instant.now()))
            throw new BadCredentialsException("Account temporarily locked");
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            user.setFailedLoginAttempts(user.getFailedLoginAttempts()+1);
            if (user.getFailedLoginAttempts()>=5) user.setLockedUntil(Instant.now().plusSeconds(900));
            users.save(user);
            throw new BadCredentialsException("Invalid credentials");
        }
        user.setFailedLoginAttempts(0); user.setLockedUntil(null); user.setLastLoginAt(Instant.now()); users.save(user);
        return issueTokens(user);
    }

    @Transactional
    public AuthDtos.TokenResponse refresh(String raw) {
        String hash=hash(raw);
        RefreshToken stored=refreshTokens.findByTokenHash(hash).orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        if (stored.getRevokedAt()!=null || stored.getExpiresAt().isBefore(Instant.now()))
            throw new BadCredentialsException("Refresh token revoked or expired");
        stored.setRevokedAt(Instant.now());
        return issueTokens(stored.getUser());
    }

    @Transactional
    public void logout(String raw) {
        refreshTokens.findByTokenHash(hash(raw)).ifPresent(t -> t.setRevokedAt(Instant.now()));
    }

    @Transactional
    public void changePassword(UUID userId, AuthDtos.ChangePasswordRequest request) {
        AppUser user=users.findById(userId).orElseThrow(() -> new NotFoundException("User not found"));
        if (!passwordEncoder.matches(request.currentPassword(), user.getPasswordHash()))
            throw new BadCredentialsException("Current password is invalid");
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        users.save(user);
    }

    @Transactional
    public void forgotPassword(String username) {
        PasswordResetDelivery delivery=resetDelivery.getIfAvailable();
        if(delivery==null) throw new RecoveryUnavailableException();
        AppUser user=users.findByUsernameIgnoreCase(username).orElse(null);
        if (user == null || user.getEmail()==null || user.getEmail().isBlank()) return;
        resetTokens.findAllByUserIdAndUsedAtIsNull(user.getId())
                .forEach(token -> token.setUsedAt(Instant.now()));
        String raw=randomToken();
        PasswordResetToken reset=new PasswordResetToken();
        reset.setUser(user); reset.setTokenHash(hash(raw)); reset.setExpiresAt(Instant.now().plusSeconds(1800));
        resetTokens.save(reset);
        String encodedToken=URLEncoder.encode(raw, StandardCharsets.UTF_8);
        delivery.send(user,frontendUrl+"/reset-password?token="+encodedToken);
    }

    @Transactional
    public void resetPassword(AuthDtos.ResetPasswordRequest request) {
        PasswordResetToken reset=resetTokens.findByTokenHash(hash(request.token()))
                .orElseThrow(() -> new ConflictException("Reset token is invalid"));
        if (reset.getUsedAt()!=null || reset.getExpiresAt().isBefore(Instant.now()))
            throw new ConflictException("Reset token is invalid or expired");
        AppUser user=reset.getUser();
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        user.setFailedLoginAttempts(0);
        user.setLockedUntil(null);
        reset.setUsedAt(Instant.now());
        refreshTokens.findAllByUserIdAndRevokedAtIsNull(user.getId())
                .forEach(token -> token.setRevokedAt(Instant.now()));
        users.save(user); resetTokens.save(reset);
    }

    private AuthDtos.TokenResponse issueTokens(AppUser user) {
        String refresh=randomToken();
        RefreshToken token=new RefreshToken();
        token.setUser(user); token.setTokenHash(hash(refresh)); token.setExpiresAt(Instant.now().plusSeconds(refreshDays*86400));
        refreshTokens.save(token);
        return new AuthDtos.TokenResponse(jwtService.generateAccessToken(user), refresh, jwtService.getAccessSeconds());
    }

    public PasswordEncoder passwordEncoder(){return passwordEncoder;}
    private String randomToken() {
        byte[] bytes=new byte[32]; random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
    public static String hash(String value) {
        try {
            byte[] digest=MessageDigest.getInstance("SHA-256").digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder hex=new StringBuilder();
            for(byte b:digest) hex.append(String.format("%02x", b));
            return hex.toString();
        } catch(Exception e){throw new IllegalStateException(e);}
    }
}
