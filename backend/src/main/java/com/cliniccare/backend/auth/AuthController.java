package com.cliniccare.backend.auth;

import com.cliniccare.backend.common.ApiResponse;
import com.cliniccare.backend.user.AppUser;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController @RequestMapping("/api/v1/auth")
public class AuthController {
    private final AuthService auth;
    public AuthController(AuthService auth){this.auth=auth;}

    @PostMapping("/login")
    public ApiResponse<AuthDtos.TokenResponse> login(@Valid @RequestBody AuthDtos.LoginRequest request) {
        return ApiResponse.success("Login successful", auth.login(request));
    }
    @PostMapping("/refresh")
    public ApiResponse<AuthDtos.TokenResponse> refresh(@Valid @RequestBody AuthDtos.RefreshRequest request) {
        return ApiResponse.success("Token refreshed", auth.refresh(request.refreshToken()));
    }
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@Valid @RequestBody AuthDtos.RefreshRequest request) {
        auth.logout(request.refreshToken());
        return ApiResponse.success("Logged out", null);
    }
    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody AuthDtos.ChangePasswordRequest request, Authentication authentication) {
        AppUser user=(AppUser) authentication.getDetails();
        auth.changePassword(user.getId(), request);
        return ApiResponse.success("Password changed", null);
    }
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody AuthDtos.ForgotPasswordRequest request) {
        auth.forgotPassword(request.username());
        return ApiResponse.success("If the account exists, reset instructions will be sent", null);
    }
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody AuthDtos.ResetPasswordRequest request) {
        auth.resetPassword(request);
        return ApiResponse.success("Password reset successful", null);
    }
}
