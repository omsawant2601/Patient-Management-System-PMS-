package com.cliniccare.backend.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AuthDtos {
    public record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    public record RefreshRequest(@NotBlank String refreshToken) {}
    public record ChangePasswordRequest(@NotBlank String currentPassword,
                                        @NotBlank @Size(min=10,max=128) String newPassword) {}
    public record ForgotPasswordRequest(@NotBlank String username) {}
    public record ResetPasswordRequest(@NotBlank String token,
                                       @NotBlank @Size(min=10,max=128) String newPassword) {}
    public record TokenResponse(String accessToken, String refreshToken, long expiresInSeconds) {}
}
